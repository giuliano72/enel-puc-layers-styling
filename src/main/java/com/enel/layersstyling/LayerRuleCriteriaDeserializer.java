package com.enel.layersstyling;

import com.enel.layersstyling.entities.LayerRuleCriteria;
import com.enel.layersstyling.entities.LayerRuleCriteriaImpl;
import com.enel.layersstyling.entities.LayerRuleRefCriteria;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LayerRuleCriteriaDeserializer extends JsonDeserializer<LayerRuleCriteria> {

    private final Logger logger = LoggerFactory.getLogger(LayerRuleCriteriaDeserializer.class);

    @Override
    public LayerRuleCriteria deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        logger.warn("Deserializing LayerRuleCriteria ...");

        try {
            JsonNode node = parser.getCodec().readTree(parser);

            JsonNode ruleRefNode = node.get("ruleRef");
            if (ruleRefNode != null) {
                String ruleRef = ruleRefNode.asText();
                if (ruleRef != null){
                    logger.warn("Deserializing LayerRuleCriteria ... ruleRef: {}", ruleRef);
                    return this.deserializeLayerRuleRefCriteria(node);

                } else {
                    return this.deserializeLayerRuleCriteriaImpl(node);
                }

            } else {
                return this.deserializeLayerRuleCriteriaImpl(node);
            }

        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            logger.error("ERROR Deserializing LayerRuleCriteria ...");
            throw new RuntimeException(ex);
        }
    }

    private LayerRuleRefCriteria deserializeLayerRuleRefCriteria(JsonNode node) {
        Long id = node.get("id").asLong();
        id = (id == 0) ? null : id;

        Integer listOrder =  node.get("listOrder").asInt();
        String criteriaOperator = node.get("criteriaOperator").asText();
        String ruleRef = node.get("ruleRef").asText();
        return new LayerRuleRefCriteria(id, listOrder, criteriaOperator, ruleRef);
    }

    private LayerRuleCriteriaImpl deserializeLayerRuleCriteriaImpl(JsonNode node) {

        Long id = node.get("id").asLong();
        id = (id == 0) ? null : id;

        Integer listOrder =  node.get("listOrder").asInt();
        String criteriaOperator = node.get("criteriaOperator").asText();
        String field = node.get("field").asText();
        String value = node.get("value").asText();
        String fieldType = node.get("fieldType").asText();
        String operator = node.get("operator").asText();

        return new LayerRuleCriteriaImpl(id, listOrder, criteriaOperator, field, fieldType, value, operator);
    }
}
