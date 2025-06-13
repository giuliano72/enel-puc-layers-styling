package com.enel.layersstyling.controllers;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.enel.layersstyling.entities.LayerRule;
import com.enel.layersstyling.models.UpdateStyleDefinitionsStyleRule;
import com.enel.layersstyling.repositories.StyleDefinitonRepository;
import com.enel.layersstyling.entities.StyleDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@RestController
@RequestMapping(value = "/definitions", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,  RequestMethod.OPTIONS})
class StyleDefinitionController {

    private final Logger logger = LoggerFactory.getLogger(StyleDefinitionController.class);
    private final StyleDefinitonRepository styleDefinitonRepository;

    @PersistenceContext
    private EntityManager entityManager;

    StyleDefinitionController(StyleDefinitonRepository styleDefinitonRepository) {
        this.styleDefinitonRepository = styleDefinitonRepository;
    }



    @GetMapping("")
    public ResponseEntity<StyleDefinition[]> listStyleDefinitions(
            @RequestParam() String layerGroup,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String environment
    ) {
        logger.debug("Executing listStyleDefinitions ...");
        List<StyleDefinition> styleDefinitions;

        if (country == null && environment == null) {
            styleDefinitions = this.styleDefinitonRepository.findByLayerGroupAndCountryAndEnvironment(layerGroup, null, null);

        } else if ( country != null && environment == null){
            styleDefinitions = this.styleDefinitonRepository.findByLayerGroupAndCountryAndEnvironment(layerGroup, country, null);

        } else if ( country == null && environment != null){
            styleDefinitions = this.styleDefinitonRepository.findByLayerGroupAndCountryAndEnvironment(layerGroup, null, environment);

        } else {
            styleDefinitions = new ArrayList<>();
        }

        // List<StyleDefinition> styleDefinitions = this.styleDefinitonRepository.findAll();
        int count = styleDefinitions.size();
        return new ResponseEntity<StyleDefinition[]>(styleDefinitions.toArray(new StyleDefinition[count]), HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Boolean> deleteStyleDefinitions(
            @RequestParam(required = false) String layerGroup,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String environment
    ) {
        logger.debug("Executing deleteStyleDefinitions ...");

        if (layerGroup == null){
            this.styleDefinitonRepository.deleteAll();

        } else if ( country != null && environment == null){
            this.styleDefinitonRepository.deleteByLayerGroupAndCountryAndEnvironment(layerGroup, country, null);

        } else if ( country == null && environment != null){
            this.styleDefinitonRepository.deleteByLayerGroupAndCountryAndEnvironment(layerGroup, null, environment);

        } else {
            this.styleDefinitonRepository.deleteByLayerGroupAndCountryAndEnvironment(layerGroup, null, null);
        }

        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @PutMapping("{styleDefinitionId}/styleRules")
    public ResponseEntity<List<LayerRule>> updateStyleDefinitionsStyleRules(
            @RequestBody UpdateStyleDefinitionsStyleRule value,
            @PathVariable Long styleDefinitionId
    ) {
        logger.debug("Executing updateStyleDefinitionsStyleRules {} ...", styleDefinitionId);

        StyleDefinition styleDefinition = this.styleDefinitonRepository.findById(styleDefinitionId).orElse(null);

        if (styleDefinition == null){
            return new ResponseEntity<List<LayerRule>>(HttpStatus.NOT_FOUND);
        }

        if (value == null){
            return new ResponseEntity<List<LayerRule>>(HttpStatus.BAD_REQUEST);
        }

        if(value.getStyleRuleId() == null){
            return new ResponseEntity<List<LayerRule>>(HttpStatus.BAD_REQUEST);
        }

        int styleRuleIndex = -1;

        for (int index = 0; index < styleDefinition.getStyleRules().size(); index++){
            if (styleDefinition.getStyleRules().get(index).getId().equals(value.getStyleRuleId())) {
                styleRuleIndex = index;
                break;
            }
        }

        if (styleRuleIndex == -1) {
            return new ResponseEntity<List<LayerRule>>(HttpStatus.NOT_FOUND);
        }

        if(value.getAction().equals("moveDown")) {

            if (styleRuleIndex == styleDefinition.getStyleRules().size() - 1) {
                logger.error("ERROR Executing updateStyleDefinitionsStyleRules {} INDEX OUT OF END BOUNDS...", styleRuleIndex);
                return new ResponseEntity<List<LayerRule>>(HttpStatus.BAD_REQUEST);
            }

            LayerRule current = styleDefinition.getStyleRules().get( styleRuleIndex);
            LayerRule next = styleDefinition.getStyleRules().get( styleRuleIndex + 1);
            Integer nextOrder = next.getListOrder();
            next.setListOrder(current.getListOrder());
            current.setListOrder(nextOrder);
            styleDefinition.getStyleRules().set(styleRuleIndex + 1, current);
            styleDefinition.getStyleRules().set(styleRuleIndex, next);

        } else if(value.getAction().equals("moveUp")) {

            if (styleRuleIndex == 0) {
                logger.error("ERROR Executing updateStyleDefinitionsStyleRules {} INDEX OUT OF START BOUNDS...", styleRuleIndex);
                return new ResponseEntity<List<LayerRule>>(HttpStatus.BAD_REQUEST);
            }

            LayerRule current = styleDefinition.getStyleRules().get( styleRuleIndex);
            LayerRule previous = styleDefinition.getStyleRules().get( styleRuleIndex - 1);
            Integer previousOrder = previous.getListOrder();
            previous.setListOrder(current.getListOrder());
            current.setListOrder(previousOrder);
            styleDefinition.getStyleRules().set(styleRuleIndex - 1, current);
            styleDefinition.getStyleRules().set(styleRuleIndex, previous);
        }

        return new ResponseEntity<List<LayerRule>>(styleDefinition.getStyleRules(), HttpStatus.OK);
    }


    @PostMapping("import")
    public ResponseEntity<Integer> importStyleDefinitions(
            @RequestBody ArrayList<Map<String, Object>> values,
            @RequestParam() String layerGroup,
            @RequestParam() String tags,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String environment
    ) {
        logger.debug("Executing importStyleDefinitions ...");
        logger.debug("Imported values: {}", values.size());

        List<StyleDefinition> styleDefinitions = new ArrayList<StyleDefinition>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            if (!values.isEmpty()) {
               values.forEach(value -> {
                   value.put("layerGroup", layerGroup);
                   value.put("tags", tags);

                   if (country != null) {
                       value.put("country", country);

                   } else if (environment != null) {
                       value.put("environment", environment);
                   }

                   StyleDefinition styleDefinition = new StyleDefinition(value);

                   List<StyleDefinition> persistedStyleDefinitions = this.styleDefinitonRepository.findByLayerGroupAndCountryAndEnvironmentAndClassIds(layerGroup, country, environment, styleDefinition.getClassIds());
                   if (persistedStyleDefinitions.isEmpty()) {
                       this.entityManager.persist(styleDefinition);
                       styleDefinitions.add(styleDefinition);

                   } else {
                       StyleDefinition persistedStyleDefinition = persistedStyleDefinitions.get(0);
                       logger.debug("Skipping existing styleDefinition({}): {}", persistedStyleDefinition.getId(), persistedStyleDefinition.getClassIds());
                   }
               });
            }

            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(styleDefinitions);
            logger.debug("Imported styleDefinitions: {}", jsonString);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }

        return new ResponseEntity<Integer>(styleDefinitions.size(), HttpStatus.OK);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public String handleException(Throwable ex) {
        return "ERROR10: " + ex.getLocalizedMessage();
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<String> handleMethodNotAllowed(MethodNotAllowedException ex) {
        return new ResponseEntity<>("errorResponse", HttpStatus.METHOD_NOT_ALLOWED);
    }
}
