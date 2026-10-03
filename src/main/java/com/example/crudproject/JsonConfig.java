package com.example.crudproject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

// Campo de texto só aceita string JSON: "titulo": 123 ou true vira 400 (em vez de virar "123").
@Configuration
public class JsonConfig {

    @Bean
    public Module stringEstrita() {
        SimpleModule m = new SimpleModule();
        m.addDeserializer(String.class, new StdScalarDeserializer<>(String.class) {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.hasToken(JsonToken.VALUE_STRING)) return p.getText();
                return (String) ctxt.handleUnexpectedToken(String.class, p);
            }
        });
        return m;
    }
}
