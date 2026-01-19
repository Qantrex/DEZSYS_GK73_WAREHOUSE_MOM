package com.example.demo.warehouse;

import com.example.demo.audit.AuditLogService;
import com.example.demo.config.AppProperties;
import com.example.demo.mapping.PayloadFormat;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@ConditionalOnProperty(name = "app.role", havingValue = "warehouse")
public class WarehouseProducer {

    private final KafkaTemplate<String, String> kafka;
    private final AppProperties props;
    private final AuditLogService audit;

    public WarehouseProducer(KafkaTemplate<String, String> kafka, AppProperties props, AuditLogService audit) {
        this.kafka = kafka;
        this.props = props;
        this.audit = audit;
    }

    public String send(String payload, PayloadFormat format) {
        String warehouseId = props.getWarehouseId();
        String topic = props.stockTopicFor(warehouseId);

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, warehouseId, payload);
        record.headers().add("warehouseId", warehouseId.getBytes(StandardCharsets.UTF_8));
        record.headers().add("payloadType", format.mediaType().getBytes(StandardCharsets.UTF_8));

        kafka.send(record);

        audit.appendLine("warehouse-" + warehouseId + ".log",
                "SEND topic=" + topic + " payloadType=" + format.mediaType() + " payload=" + payload);

        return topic;
    }
}
