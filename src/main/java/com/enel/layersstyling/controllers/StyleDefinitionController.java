package com.enel.layersstyling.controllers;

import ch.qos.logback.classic.encoder.JsonEncoder;
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
    public ResponseEntity<Boolean> updateStyleDefinitionsStyleRules(
            @RequestBody UpdateStyleDefinitionsStyleRule value,
            @PathVariable Long styleDefinitionId
    ) {
        logger.debug("Executing updateStyleDefinitionsStyleRules {} ...", styleDefinitionId);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
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
