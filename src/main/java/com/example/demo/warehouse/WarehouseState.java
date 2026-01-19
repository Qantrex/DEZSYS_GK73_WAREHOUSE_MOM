package com.example.demo.warehouse;

import com.example.demo.domain.StockItem;
import com.example.demo.domain.WarehouseStock;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class WarehouseState {

    private final AtomicReference<WarehouseStock> state = new AtomicReference<>(
            new WarehouseStock("linz", Instant.now(), List.of(
                    new StockItem("SKU-100", "Toilet Paper", 120, "pcs"),
                    new StockItem("SKU-200", "Pasta", 80, "pcs")
            ))
    );

    public WarehouseStock get() { return state.get(); }

    public void set(WarehouseStock stock) { state.set(stock); }
}
