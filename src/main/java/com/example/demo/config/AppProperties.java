package com.example.demo.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotBlank
    private String role = "warehouse"; // central | warehouse

    private String warehouseId = "linz";
    private List<String> warehouseIds = new ArrayList<>();

    private Topics topics = new Topics();

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public List<String> getWarehouseIds() { return warehouseIds; }
    public void setWarehouseIds(List<String> warehouseIds) { this.warehouseIds = warehouseIds; }

    public Topics getTopics() { return topics; }
    public void setTopics(Topics topics) { this.topics = topics; }

    public static class Topics {
        private String ack = "warehouse.ack";
        private String stockTemplate = "warehouse.%s.stock";
        private String stockPattern = "warehouse\\..*\\.stock";

        public String getAck() { return ack; }
        public void setAck(String ack) { this.ack = ack; }

        public String getStockTemplate() { return stockTemplate; }
        public void setStockTemplate(String stockTemplate) { this.stockTemplate = stockTemplate; }

        public String getStockPattern() { return stockPattern; }
        public void setStockPattern(String stockPattern) { this.stockPattern = stockPattern; }
    }

    public String stockTopicFor(String warehouseId) {
        return String.format(this.topics.stockTemplate, warehouseId);
    }
}
