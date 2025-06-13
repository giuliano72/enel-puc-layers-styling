package com.enel.layersstyling;

import com.enel.layersstyling.entities.LayerRuleCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class LayersStylingApplication {
    private final Logger logger = LoggerFactory.getLogger(LayersStylingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LayersStylingApplication.class, args);
    }

    /*@Bean
    public ObjectMapper registerObjectMapper(){
        logger.warn("Registering ObjectMapper ...");

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("LayerRuleCriteriaSerializer");
        module.addSerializer(LayerRuleCriteria.class, new LayerRuleCriteriaSerializer(this.logger));
        module.addDeserializer(LayerRuleCriteria.class, new LayerRuleCriteriaDeserializer(this.logger));
        mapper.registerModule(module);

        return mapper;
    }*/
}
