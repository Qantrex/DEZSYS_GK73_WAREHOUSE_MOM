package com.example.demo.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "centralStockReport")
public class CentralStockReport {

    private Instant generatedAt = Instant.now();

    @JacksonXmlElementWrapper(localName = "warehouses")
    private List<WarehouseStock> warehouses = new ArrayList<>();

    public CentralStockReport() {}

    public CentralStockReport(Instant generatedAt, List<WarehouseStock> warehouses) {
        this.generatedAt = generatedAt;
        this.warehouses = warehouses;
    }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }

    public List<WarehouseStock> getWarehouses() { return warehouses; }
    public void setWarehouses(List<WarehouseStock> warehouses) { this.warehouses = warehouses; }
}
