package io.albot.lims.report.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.app.VelocityEngine;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicroBiomesModelMapperConfig {
    ModelMapper modelMapper = new ModelMapper();
    VelocityEngine velocityEngine = new VelocityEngine();

    @Bean
    public ModelMapper modelMapper() {

        return this.modelMapper;
    }


    @Bean
    public VelocityEngine velocityEngine() {
        return this.velocityEngine;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
