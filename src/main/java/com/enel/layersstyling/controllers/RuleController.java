package com.enel.layersstyling.controllers;

import com.enel.layersstyling.entities.LayerRule;
import com.enel.layersstyling.entities.StyleDefinition;
import com.enel.layersstyling.repositories.RuleRepository;
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
@RequestMapping(value = "/rules", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,  RequestMethod.OPTIONS})
class RuleController {

    private final Logger logger = LoggerFactory.getLogger(RuleController.class);
    private final RuleRepository ruleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @GetMapping("")
    public ResponseEntity<LayerRule[]> listRules(
            @RequestParam() String layerGroup,
            @RequestParam(required = false) String environment
    ) {
        logger.debug("Executing listRules ...");
        List<LayerRule> rules;

        if (environment == null) {
            rules = this.ruleRepository.findByLayerGroupAndEnvironmentAndLinked(layerGroup, null, Boolean.FALSE);

        } else {
            rules = this.ruleRepository.findByLayerGroupAndEnvironmentAndLinked(layerGroup, environment, Boolean.FALSE);
        }

        int count = rules.size();
        return new ResponseEntity<LayerRule[]>(rules.toArray(new LayerRule[count]), HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Boolean> deleteRules(
            @RequestParam(required = false) String layerGroup,
            @RequestParam(required = false) String environment
    ) {
        logger.debug("Executing deleteRules ...");

        if (layerGroup == null && environment == null) {
            this.ruleRepository.deleteByLinked(Boolean.FALSE);

        } else this.ruleRepository.deleteByLayerGroupAndEnvironmentAndLinked(layerGroup, environment, Boolean.FALSE);

        return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
    }

    @PostMapping("import")
    public ResponseEntity<Integer> importRules(
            @RequestBody ArrayList<Map<String, Object>> values,
            @RequestParam() String layerGroup,
            @RequestParam(required = false) String environment
    ) {
        logger.debug("Executiong importRules ...");
        logger.debug("Imported values: {}", values.size());

        List<LayerRule> layerRules = new ArrayList<LayerRule>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            if (!values.isEmpty()) {
                values.forEach(value -> {
                    value.put("layerGroup", layerGroup);
                    value.put("linked", Boolean.FALSE);

                    if (environment != null) {
                        value.put("environment", environment);
                    }

                    LayerRule layerRule = new LayerRule(value);

                    List<LayerRule> persistedLayerRules = this.ruleRepository.findByLayerGroupAndEnvironmentAndLinkedAndValue(layerGroup, environment, Boolean.FALSE, layerRule.getValue());
                    if (persistedLayerRules.isEmpty()) {
                        this.entityManager.persist(layerRule);
                        layerRules.add(layerRule);

                    } else {
                        LayerRule persistedLayerRule =  persistedLayerRules.get(0);
                        logger.debug("Skipping existing layerRule({}): {}", persistedLayerRule.getId(), persistedLayerRule.getValue());
                    }
                });
            }

            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(layerRules);
            logger.debug("Imported layerRules: {}", jsonString);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }

        return new ResponseEntity<Integer>(layerRules.size(), HttpStatus.OK);
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
