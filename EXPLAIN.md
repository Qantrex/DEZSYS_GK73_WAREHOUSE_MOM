# Project Code Explanation

This document explains the purpose and key components of the important files in this distributed warehouse system.

## 1. Configuration: `src/main/resources/application.yml`

This is the central configuration file. It defines how the application behaves based on the active profile/role.

**Key Parts:**
- `app.role`: Determines if the instance runs as **central** or **warehouse**.
- `app.warehouse-ids`: A list of known warehouses. The central component uses this to create Kafka topics on startup.
- `spring.kafka`: Standard Spring Kafka properties (bootstrap servers, consumer groups).

## 2. Infrastructure: `infra/docker-compose.yml`

Defines the external dependencies required to run the system.
- **kafka**: The Apache Kafka broker (listening on 9092).
- **kafka-ui**: A web interface for monitoring Kafka topics and messages (listening on 8085).

## 3. Central Component

The central component aggregates stock data from all warehouses.

### `com.example.demo.central.CentralConsumer.java`
This service listens to Kafka topics for incoming stock updates.

**Key Code:**
```java
@KafkaListener(topicPattern = "${app.topics.stock-pattern}", groupId = "central-collector")
public void onStock(ConsumerRecord<String, String> record) { ... }
```
- It uses a `topicPattern` (regex) to subscribe to all topics matching `warehouse.*.stock`.
- After processing the stock, it sends an acknowledgement back to the `warehouse.ack` topic.

### `com.example.demo.config.KafkaTopicConfig.java`
This configuration class is responsible for creating the necessary Kafka topics automatically when the central component starts.

**Key Code:**
```java
@Bean
public KafkaAdmin.NewTopics centralTopics(AppProperties props) { ... }
```
- Iterates over `app.warehouse-ids` to create a `warehouse.<id>.stock` topic for each.
- Creates the shared `warehouse.ack` topic.

## 4. Warehouse Component

The warehouse component manages stock for a specific location.

### `com.example.demo.warehouse.WarehouseProducer.java`
This service is responsible for sending stock updates to the central system via Kafka.

**Key Code:**
```java
public String send(String payload, PayloadFormat format) {
    // ...
    ProducerRecord<String, String> record = new ProducerRecord<>(topic, warehouseId, payload);
    kafka.send(record);
    // ...
}
```
- It constructs a `ProducerRecord` with the target topic (`warehouse.<id>.stock`) and the message payload (JSON or XML).
- It adds custom headers (`warehouseId`, `payloadType`) for metadata.

### `com.example.demo.warehouse.WarehouseAckListener.java`
This listener waits for confirmation from the central component that the stock update was received.

**Key Code:**
```java
@KafkaListener(topics = "${app.topics.ack}", groupId = "warehouse-ack-${app.warehouse-id}")
public void onAck(ConsumerRecord<String, String> record) {
    if (record.key().equals(props.getWarehouseId())) {
       // ... process ack
    }
}
```
- It listens to the `warehouse.ack` topic.
- It filters messages by `key` to ensure it only processes ACKs intended for *this* specific warehouse.

## 5. REST Controllers

Both roles verify against the REST API.

- **`WarehouseController.java`**:
  - `GET /warehouse/stock`: Returns current stock.
  - `POST /warehouse/send`: Triggers the Kafka message send.
- **`CentralController.java`**:
  - `GET /central/stocks`: Returns the aggregated stock report from all warehouses.

## 6. Data Storage & Generation

### `com.example.demo.warehouse.WarehouseState.java`
This service holds the current state of the warehouse's stock in memory.

**Key Code:**
```java
// Logic injected via constructor
private void initializeStock() {
    String id = props.getWarehouseId().toLowerCase();
    // switch (id) { ... assign specific items ... }
}
```
- **Stock Generation**: The stock is generated dynamically based on the configured `app.warehouse-id`. Different locations (e.g., 'linz' vs 'wien') get different initial items.
- **Persistence**: There is no database. The state is kept in memory using an `AtomicReference` and is lost when the application restarts.
- **Updates**: Updates are applied via the `PUT /warehouse/stock` endpoint (REST), which overwrites the in-memory state.
