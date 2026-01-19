package com.example.demo.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class StockItem {

    @JacksonXmlProperty(isAttribute = true)
    private String sku;

    private String name;
    private int quantity;
    private String unit;

    public StockItem() {}

    public StockItem(String sku, String name, int quantity, String unit) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
