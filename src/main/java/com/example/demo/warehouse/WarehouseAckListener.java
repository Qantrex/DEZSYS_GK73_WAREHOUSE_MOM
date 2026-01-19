package com.example.demo.warehouse;

import com.example.demo.audit.AuditLogService;
import com.example.demo.config.AppProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@ConditionalOnProperty(name = "app.role", havingValue = "warehouse")
public class WarehouseAckListener {

    private final AppProperties props;
    private final AuditLogService audit;
    private final AtomicReference<String> lastAck = new AtomicReference<>("NONE");

    public WarehouseAckListener(AppProperties props, AuditLogService audit) {
        this.props = props;
        this.audit = audit;
    }

    @KafkaListener(
        topics = "${app.topics.ack}",
        groupId = "warehouse-ack-${app.warehouse-id}"
    )
    public void onAck(ConsumerRecord<String, String> record) {
        // Key = warehouseId, damit jeder Standort nur seine ACKs konsumiert
        if (record.key() == null || !record.key().equals(props.getWarehouseId())) return;

        lastAck.set(record.value());
        audit.appendLine("warehouse-" + props.getWarehouseId() + ".log",
                "RECV_ACK topic=" + record.topic() + " key=" + record.key() + " value=" + record.value());
    }

    public String getLastAck() {
        return lastAck.get();
    }
}
