package com.enel.layersstyling.controllers;

import com.enel.layersstyling.entities.LayerRule;
import com.enel.layersstyling.models.MoveStyleDefinitionsStyleRule;
import com.enel.layersstyling.repositories.StyleDefinitonRepository;
import com.enel.layersstyling.entities.StyleDefinition;
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

    @PostMapping("new")
    public ResponseEntity<StyleDefinition> newStyleDefinition(
            @RequestBody StyleDefinition styleDefinition
    ) {
        this.entityManager.persist(styleDefinition);
        return new ResponseEntity<StyleDefinition>(styleDefinition, HttpStatus.OK);
    }

    @PutMapping("{styleDefinitionId}/moveToCountry")
    public ResponseEntity<Boolean> moveToCountry(
            @RequestBody String country,
            @PathVariable(required = true) Long styleDefinitionId
    ) {
        logger.debug("Executing moveToCountry({}) -> {} ...", styleDefinitionId, country);

        StyleDefinition styleDefinition = this.styleDefinitonRepository.getReferenceById(styleDefinitionId);
        styleDefinition.setCountry(country);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @PutMapping("{styleDefinitionId}/moveToEnvironment")
    public ResponseEntity<Boolean> moveToEnvironment(
            @RequestBody String environment,
            @PathVariable(required = true) Long styleDefinitionId
    ) {
        logger.debug("Executing moveToEnvironment({}) -> {} ...", styleDefinitionId, environment);

        StyleDefinition styleDefinition = this.styleDefinitonRepository.getReferenceById(styleDefinitionId);
        styleDefinition.setEnvironment(environment);
        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @DeleteMapping("{styleDefinitionId}/delete")
    public ResponseEntity<Boolean> deleteStyleDefinition(
            @PathVariable(required = true) Long styleDefinitionId
    ) {
        logger.debug("Executing deleteStyleDefinitions({}) ...", styleDefinitionId);

        StyleDefinition styleDefinition = this.styleDefinitonRepository.getReferenceById(styleDefinitionId);
        boolean isGlobal = (styleDefinition.getCountry() == null && styleDefinition.getEnvironment() == null);

        if (isGlobal){
            logger.debug("StyleDefinitonRepository deleteByLayerGroupAndClassIds({}, {}) ...", styleDefinition.getLayerGroup(), styleDefinition.getClassIds());
            this.styleDefinitonRepository.deleteByLayerGroupAndClassIds(styleDefinition.getLayerGroup(), styleDefinition.getClassIds());

        } else {
            logger.debug("StyleDefinitonRepository deleteById({}) ...", styleDefinition.getId());
            this.styleDefinitonRepository.deleteById(styleDefinitionId);
        }

        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
     }

    @DeleteMapping("delete")
    public ResponseEntity<Boolean> deleteStyleDefinitions(
            @RequestParam(required = false) String layerGroup,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String environment
    ) {
        logger.debug("Executing deleteStyleDefinitions ...");

        if (layerGroup == null){
            logger.debug("StyleDefinitonRepository deleteAll ...");
            this.styleDefinitonRepository.deleteAll();

        } else if ( country != null && environment == null){
            logger.debug("StyleDefinitonRepository deleteByLayerGroupAndCountryAndEnvironment({}, {}, {}) ...", layerGroup, country, null);
            this.styleDefinitonRepository.deleteByLayerGroupAndCountryAndEnvironment(layerGroup, country, null);

        } else if ( country == null && environment != null){
            logger.debug("StyleDefinitonRepository deleteByLayerGroupAndCountryAndEnvironment({}, {}, {}) ...", layerGroup, null, environment);
            this.styleDefinitonRepository.deleteByLayerGroupAndCountryAndEnvironment(layerGroup, null, environment);

        } else {
            logger.debug("StyleDefinitonRepository deleteByLayerGroup({}) ...", layerGroup);
            this.styleDefinitonRepository.deleteByLayerGroup(layerGroup);
        }

        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @DeleteMapping("{styleDefinitionId}/styleRules/{styleRuleId}/delete")
    public ResponseEntity<List<LayerRule>> deleteStyleDefinitionStyleRule(
            @PathVariable(required = true) Long styleDefinitionId,
            @PathVariable(required = true) Long styleRuleId
    ) {
        logger.debug("Executing deleteStyleDefinitionStyleRule ...");

        StyleDefinition styleDefinition = this.styleDefinitonRepository.findById(styleDefinitionId).orElse(null);

        if (styleDefinition == null){
            return new ResponseEntity<List<LayerRule>>(HttpStatus.NOT_FOUND);
        }

        LayerRule styleRule = null;
        int index = 0;
        int styleRuleIndex = 0;
        for(LayerRule item: styleDefinition.getStyleRules()) {
            if (item.getId().equals(styleRuleId)) {
                styleRule = item;
                styleRuleIndex = index;
            }
            index++;
        }

        if (styleRule == null){
            return new ResponseEntity<List<LayerRule>>(HttpStatus.NOT_FOUND);
        }

        styleDefinition.getStyleRules().remove(styleRuleIndex);

        return new ResponseEntity<List<LayerRule>>(styleDefinition.getStyleRules(), HttpStatus.OK);
    }

    @PostMapping("{styleDefinitionId}/styleRules/add")
    public ResponseEntity<List<LayerRule>> addStyleRule(
            @RequestBody LayerRule newStyleRule,
            @PathVariable Long styleDefinitionId
    ) {
        logger.debug("Executing addStyleRule styleDefinitionId: {} ...", styleDefinitionId);

        StyleDefinition styleDefinition = this.styleDefinitonRepository.findById(styleDefinitionId).orElse(null);

        if(styleDefinition == null){
            return new ResponseEntity<List<LayerRule>>(HttpStatus.NOT_FOUND);
        }

        if(newStyleRule == null){
            return new ResponseEntity<List<LayerRule>>(HttpStatus.BAD_REQUEST);
        }

        this.entityManager.persist(newStyleRule);
        styleDefinition.getStyleRules().add(newStyleRule);


        return new ResponseEntity<List<LayerRule>>(styleDefinition.getStyleRules(), HttpStatus.OK);
    }

    @PutMapping("{styleDefinitionId}/styleRules/update")
    public ResponseEntity<LayerRule> updateStyleRule(
            @RequestBody LayerRule updatedStyleRule,
            @PathVariable Long styleDefinitionId
    ) {
        logger.debug("Executing updateStyleRule styleDefinitionId: {} ...", styleDefinitionId);

        StyleDefinition styleDefinition = this.styleDefinitonRepository.findById(styleDefinitionId).orElse(null);

        if(styleDefinition == null){
            return new ResponseEntity<LayerRule>(HttpStatus.NOT_FOUND);
        }

        LayerRule styleRule = null;
        for(int index = 0; index < styleDefinition.getStyleRules().size(); index++){
            if (styleDefinition.getStyleRules().get(index).getId().equals(updatedStyleRule.getId())){
                styleRule = styleDefinition.getStyleRules().get(index);
            }
        }

        if(styleRule == null){
            return new ResponseEntity<LayerRule>(HttpStatus.NOT_FOUND);
        }

        logger.debug("Executing updateStyleRule styleRuleId: {} ...", styleRule.getId());
        styleRule.update(updatedStyleRule, this.entityManager);

        return new ResponseEntity<LayerRule>(styleRule, HttpStatus.OK);
    }

    @PutMapping("{styleDefinitionId}/styleRules/moveStyleRule")
    public ResponseEntity<List<LayerRule>> moveStyleRule(
            @RequestBody MoveStyleDefinitionsStyleRule value,
            @PathVariable Long styleDefinitionId
    ) {



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

        Long styleRuleId = value.getStyleRuleId();
        logger.debug("Executing moveStyleRule styleRuleId: {}, styleDefinitionId: {} ...", styleRuleId,  styleDefinitionId);

        int styleRuleIndex = -1;

        for (int index = 0; index < styleDefinition.getStyleRules().size(); index++){
            Long currentStyleRuleId = styleDefinition.getStyleRules().get(index).getId();

            logger.debug("Executing moveStyleRule currentStyleRuleId: {}, styleRuleId: {} ...", currentStyleRuleId,  styleRuleId);

            if (styleDefinition.getStyleRules().get(index).getId().equals(value.getStyleRuleId())) {
                styleRuleIndex = index;
                logger.debug("Executing moveStyleRule found styleRuleId: {}({}) at index: {}", styleRuleId, styleDefinition.getStyleRules().get(index).getValue(), styleRuleIndex);
                break;
            }
        }

        if (styleRuleIndex == -1) {
            return new ResponseEntity<List<LayerRule>>(HttpStatus.NOT_FOUND);
        }

        if(value.getDirection().equals("down")) {

            if (styleRuleIndex == styleDefinition.getStyleRules().size() - 1) {
                logger.error("ERROR Executing moveStyleRule {} INDEX OUT OF END BOUNDS... ", styleRuleIndex);
                return new ResponseEntity<List<LayerRule>>(HttpStatus.BAD_REQUEST);
            }

            LayerRule current = styleDefinition.getStyleRules().get( styleRuleIndex);
            LayerRule next = styleDefinition.getStyleRules().get( styleRuleIndex + 1);
            Integer nextOrder = Integer.valueOf(next.getListOrder().toString());

            next.setListOrder(current.getListOrder());
            current.setListOrder(nextOrder);

            styleDefinition.getStyleRules().sort((o1, o2) -> {
                Integer order1 = o1.getListOrder();
                Integer order2 = o2.getListOrder();
                return order1.compareTo(order2);
            });

        } else if(value.getDirection().equals("up")) {

            if (styleRuleIndex == 0) {
                logger.error("ERROR Executing moveStyleRule {} INDEX OUT OF START BOUNDS... ", styleRuleIndex);
                return new ResponseEntity<List<LayerRule>>(HttpStatus.BAD_REQUEST);
            }

            LayerRule current = styleDefinition.getStyleRules().get( styleRuleIndex);
            LayerRule previous = styleDefinition.getStyleRules().get( styleRuleIndex - 1);
            Integer previousOrder = Integer.valueOf(previous.getListOrder().toString());

            previous.setListOrder(current.getListOrder());
            current.setListOrder(previousOrder);

            styleDefinition.getStyleRules().sort((o1, o2) -> {
                Integer order1 = o1.getListOrder();
                Integer order2 = o2.getListOrder();
                return order1.compareTo(order2);
            });
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
