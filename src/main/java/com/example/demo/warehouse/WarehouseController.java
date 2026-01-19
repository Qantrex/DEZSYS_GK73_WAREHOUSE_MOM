package com.example.demo.warehouse;

import com.example.demo.config.AppProperties;
import com.example.demo.domain.WarehouseStock;
import com.example.demo.mapping.PayloadFormat;
import com.example.demo.mapping.PayloadMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@ConditionalOnProperty(name = "app.role", havingValue = "warehouse")
public class WarehouseController {

    private final AppProperties props;
    private final WarehouseState state;
    private final WarehouseProducer producer;
    private final PayloadMapper mapper;
    private final WarehouseAckListener ack;

    public WarehouseController(AppProperties props, WarehouseState state, WarehouseProducer producer, PayloadMapper mapper, WarehouseAckListener ack) {
        this.props = props;
        this.state = state;
        this.producer = producer;
        this.mapper = mapper;
        this.ack = ack;
    }

    @GetMapping(value = "/warehouse/stock", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public WarehouseStock getStock() {
        WarehouseStock stock = state.get();
        stock.setWarehouseId(props.getWarehouseId());
        return stock;
    }

    @PutMapping(value = "/warehouse/stock", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public void putStock(@RequestBody String raw, @RequestHeader(value="Content-Type", required=false) String contentType) throws Exception {
        PayloadFormat fmt = PayloadFormat.fromContentType(contentType);
        WarehouseStock parsed = mapper.parse(raw, fmt);
        parsed.setWarehouseId(props.getWarehouseId());
        state.set(parsed);
    }

    /**
     * Trigger wie in Angabe: /warehouse/send => sendet JSON oder XML in die "Queue" (Kafka Topic)
     * Format via ?format=json|xml oder Accept Header.
     */
    @PostMapping(value = "/warehouse/send", produces = MediaType.TEXT_PLAIN_VALUE)
    public String send(@RequestParam(value = "format", required = false) String format,
                       @RequestHeader(value = "Accept", required = false) String accept) throws Exception {

        PayloadFormat fmt = chooseFormat(format, accept);

        WarehouseStock stock = state.get();
        stock.setWarehouseId(props.getWarehouseId());
        String payload = mapper.serialize(stock, fmt);

        String topic = producer.send(payload, fmt);
        return "SENT warehouseId=" + props.getWarehouseId() + " topic=" + topic + " payloadType=" + fmt.mediaType();
    }

    @GetMapping(value = "/warehouse/last-ack", produces = MediaType.TEXT_PLAIN_VALUE)
    public String lastAck() {
        return "warehouseId=" + props.getWarehouseId() + " lastAck=" + ack.getLastAck();
    }

    private PayloadFormat chooseFormat(String format, String accept) {
        if (format != null) {
            if (format.equalsIgnoreCase("xml")) return PayloadFormat.XML;
            return PayloadFormat.JSON;
        }
        if (accept != null && accept.toLowerCase().contains("xml")) return PayloadFormat.XML;
        return PayloadFormat.JSON;
    }
}
