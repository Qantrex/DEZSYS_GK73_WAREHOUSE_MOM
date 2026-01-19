package com.example.demo.mapping;

import com.example.demo.domain.WarehouseStock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

@Component
public class PayloadMapper {

    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public PayloadMapper(ObjectMapper springObjectMapper) {
        // Spring Boot’s ObjectMapper already has JavaTimeModule configured
        this.jsonMapper = springObjectMapper.copy();

        this.xmlMapper = new XmlMapper();
        this.xmlMapper.registerModule(new JavaTimeModule());
        this.xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public WarehouseStock parse(String raw, PayloadFormat format) throws Exception {
        if (format == PayloadFormat.XML) {
            return xmlMapper.readValue(raw, WarehouseStock.class);
        }
        return jsonMapper.readValue(raw, WarehouseStock.class);
    }

    public String serialize(WarehouseStock stock, PayloadFormat format) throws Exception {
        if (format == PayloadFormat.XML) {
            return xmlMapper.writeValueAsString(stock);
        }
        return jsonMapper.writeValueAsString(stock);
    }
}
