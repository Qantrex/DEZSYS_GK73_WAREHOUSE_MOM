package com.example.demo.central;

import com.example.demo.domain.CentralStockReport;
import com.example.demo.domain.WarehouseStock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@ConditionalOnProperty(name = "app.role", havingValue = "central")
public class CentralController {

    private final CentralStockService service;

    public CentralController(CentralStockService service) {
        this.service = service;
    }

    @GetMapping(value = "/central/stocks", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public CentralStockReport all() {
        return service.report();
    }

    @GetMapping(value = "/central/stocks/{warehouseId}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public WarehouseStock one(@PathVariable String warehouseId) {
        return service.getOne(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown warehouseId: " + warehouseId));
    }
}
