package com.example.demo.central;

import com.example.demo.domain.CentralStockReport;
import com.example.demo.domain.WarehouseStock;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CentralStockService {

    private final Map<String, WarehouseStock> latest = new ConcurrentHashMap<>();

    public void upsert(WarehouseStock stock) {
        latest.put(stock.getWarehouseId(), stock);
    }

    public Optional<WarehouseStock> getOne(String warehouseId) {
        return Optional.ofNullable(latest.get(warehouseId));
    }

    public CentralStockReport report() {
        List<WarehouseStock> list = new ArrayList<>(latest.values());
        list.sort(Comparator.comparing(WarehouseStock::getWarehouseId));
        return new CentralStockReport(Instant.now(), list);
    }
}
