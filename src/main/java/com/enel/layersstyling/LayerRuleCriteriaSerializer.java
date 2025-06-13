package com.enel.layersstyling;

import com.enel.layersstyling.entities.LayerRuleCriteria;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LayerRuleCriteriaSerializer extends JsonSerializer<LayerRuleCriteria> {

    private final Logger logger = LoggerFactory.getLogger(LayerRuleCriteriaSerializer.class);

    @Override
    public void serialize(LayerRuleCriteria value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        logger.warn("Serializing LayerRuleCriteria ...");
        new ObjectMapper().writer().writeValue(jsonGenerator, value);
    }
}
