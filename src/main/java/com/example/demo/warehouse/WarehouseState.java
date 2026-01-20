package com.example.demo.warehouse;

import com.example.demo.config.AppProperties;
import com.example.demo.domain.StockItem;
import com.example.demo.domain.WarehouseStock;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class WarehouseState {

        private final AtomicReference<WarehouseStock> state = new AtomicReference<>();
        private final AppProperties props;

        public WarehouseState(AppProperties props) {
                this.props = props;
                initializeStock();
        }

        private void initializeStock() {
                String id = props.getWarehouseId().toLowerCase();
                List<StockItem> items;

                switch (id) {
                        case "linz":
                                items = List.of(
                                                new StockItem("SKU-100", "Toilet Paper", 120, "pcs"),
                                                new StockItem("SKU-200", "Pasta", 80, "pcs"),
                                                new StockItem("SKU-300", "Linzer Hallertor", 50, "box"),
                                                new StockItem("SKU-400", "Flour", 200, "kg"));
                                break;
                        case "wien":
                                items = List.of(
                                                new StockItem("SKU-101", "Sachertorte", 20, "pcs"),
                                                new StockItem("SKU-201", "Almdudler", 200, "bottle"),
                                                new StockItem("SKU-301", "Wiener Schnitzel", 15, "portion"),
                                                new StockItem("SKU-401", "Coffee Beans", 40, "kg"));
                                break;
                        default:
                                items = List.of(
                                                new StockItem("DEF-001", "Generic Box", 100, "box"),
                                                new StockItem("DEF-002", "Unknown Item", 10, "pcs"),
                                                new StockItem("DEF-003", "Spare Parts", 5, "set"));
                                break;
                }

                state.set(new WarehouseStock(id, Instant.now(), items));
        }

        public WarehouseStock get() {
                return state.get();
        }

        public void set(WarehouseStock stock) {
                state.set(stock);
        }
}
