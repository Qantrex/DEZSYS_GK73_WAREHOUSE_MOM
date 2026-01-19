package com.example.demo.central;

import com.example.demo.audit.AuditLogService;
import com.example.demo.config.AppProperties;
import com.example.demo.domain.WarehouseStock;
import com.example.demo.mapping.PayloadFormat;
import com.example.demo.mapping.PayloadMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@ConditionalOnProperty(name = "app.role", havingValue = "central")
public class CentralConsumer {

    private final AppProperties props;
    private final PayloadMapper mapper;
    private final CentralStockService service;
    private final KafkaTemplate<String, String> kafka;
    private final AuditLogService audit;

    public CentralConsumer(AppProperties props, PayloadMapper mapper, CentralStockService service,
                           KafkaTemplate<String, String> kafka, AuditLogService audit) {
        this.props = props;
        this.mapper = mapper;
        this.service = service;
        this.kafka = kafka;
        this.audit = audit;
    }

    /**
     * Zentrale konsumiert alle Warehouse-"Queues" (Kafka Topics) über Pattern.
     * @KafkaListener / topicPattern siehe Spring Kafka Referenz. :contentReference[oaicite:4]{index=4}
     */
    @KafkaListener(topicPattern = "${app.topics.stock-pattern}", groupId = "central-collector")
    public void onStock(ConsumerRecord<String, String> record) throws Exception {

        String warehouseId = record.key() != null ? record.key() : "UNKNOWN";
        PayloadFormat fmt = payloadTypeFromHeader(record);

        // Logging der übertragenen Daten (Integrität)
        audit.appendLine("central.log",
                "RECV topic=" + record.topic() + " key=" + warehouseId + " payloadType=" + fmt.mediaType() + " payload=" + record.value());

        WarehouseStock stock = mapper.parse(record.value(), fmt);
        stock.setWarehouseId(warehouseId); // authoritative: Key
        service.upsert(stock);

        // Rückmeldung SUCCESS an Warehouse (Topic)
        kafka.send(props.getTopics().getAck(), warehouseId, "SUCCESS");
        audit.appendLine("central.log",
                "SEND_ACK topic=" + props.getTopics().getAck() + " key=" + warehouseId + " value=SUCCESS");
    }

    private PayloadFormat payloadTypeFromHeader(ConsumerRecord<String, String> record) {
        var h = record.headers().lastHeader("payloadType");
        if (h == null) return PayloadFormat.JSON;
        String v = new String(h.value(), StandardCharsets.UTF_8).toLowerCase();
        if (v.contains("xml")) return PayloadFormat.XML;
        return PayloadFormat.JSON;
    }
}
