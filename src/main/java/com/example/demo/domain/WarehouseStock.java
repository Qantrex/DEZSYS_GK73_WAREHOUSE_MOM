package com.example.demo.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "warehouseStock")
public class WarehouseStock {

    private String warehouseId;
    private Instant timestamp = Instant.now();

    @JacksonXmlElementWrapper(localName = "items")
    private List<StockItem> items = new ArrayList<>();

    public WarehouseStock() {}

    public WarehouseStock(String warehouseId, Instant timestamp, List<StockItem> items) {
        this.warehouseId = warehouseId;
        this.timestamp = timestamp;
        this.items = items;
    }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public List<StockItem> getItems() { return items; }
    public void setItems(List<StockItem> items) { this.items = items; }
}
