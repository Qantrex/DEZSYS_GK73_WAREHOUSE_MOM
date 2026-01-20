# Project Usage Guide

This project is a distributed warehouse management system simulation using Spring Boot and Apache Kafka.

## Prerequisites

-   Java 17
-   Docker & Docker Compose

## Infrastructure Setup

Start the Kafka broker and Kafka UI using Docker Compose:

```bash
cd infra
docker-compose up -d
```

-   **Kafka Broker**: `localhost:9092`
-   **Kafka UI**: [http://localhost:8085](http://localhost:8085)

## Running the Application

You need to run at least one **Central** instance and one or more **Warehouse** instances. You can run them in separate terminal windows.

### 1. Run Central Component

The central component aggregates stock data.

```bash
./gradlew bootRun --args='--app.role=central --server.port=8080'
```

-   **API**: [http://localhost:8080/central/stocks](http://localhost:8080/central/stocks)

### 2. Run Warehouse Components

You can simulate multiple warehouses by changing the `warehouse-id` and `server.port`.

**Warehouse "Linz"**:
```bash
./gradlew bootRun --args='--app.role=warehouse --app.warehouse-id=linz --server.port=8081'
```
-   **API**: [http://localhost:8081/warehouse/stock](http://localhost:8081/warehouse/stock)

**Warehouse "Vienna"**:
```bash
./gradlew bootRun --args='--app.role=warehouse --app.warehouse-id=wien --server.port=8082'
```

## Using the System

1.  **Check Stock (Warehouse)**:
    Access `http://localhost:8081/warehouse/stock` to see the current stock of the Linz warehouse.

2.  **Send Stock Update**:
    Trigger a stock update from a warehouse to the central system via Kafka.
    ```bash
    curl -X POST "http://localhost:8081/warehouse/send?format=json"
    ```

3.  **Check Aggregated Stock (Central)**:
    Access `http://localhost:8080/central/stocks` to see the collected data from all warehouses.

4.  **Monitor Kafka**:
    Open [http://localhost:8085](http://localhost:8085) to view topics (`warehouse.linz.stock`, `warehouse.ack`) and messages in real-time.

## Example Verification Session

Below is a complete example session demonstrating how to interact with the system using `curl` and what output to expect.

### 1. Check Initial Stock
```bash
curl -sS -H "Accept: application/json" "http://localhost:8081/warehouse/stock"
```
**Output:**
```json
{"warehouseId":"linz","timestamp":"...","items":[{"sku":"SKU-100","name":"Toilet Paper","quantity":120,"unit":"pcs"},{"sku":"SKU-200","name":"Pasta","quantity":80,"unit":"pcs"}]}
```

### 2. Update Stock (PUT)
```bash
curl -sS -X PUT "http://localhost:8081/warehouse/stock" \
  -H "Content-Type: application/json" \
  -d '{ "warehouseId":"linz", "timestamp":"2026-01-20T08:00:00Z", "items":[ {"sku":"SKU-1","name":"Soap","quantity":50,"unit":"pcs"}, {"sku":"SKU-2","name":"Pasta","quantity":120,"unit":"pcs"} ] }'
```

### 3. Verify Update
```bash
curl -sS -H "Accept: application/json" "http://localhost:8081/warehouse/stock"
```
**Output:**
```json
{"warehouseId":"linz","timestamp":"2026-01-20T08:00:00Z","items":[{"sku":"SKU-1","name":"Soap","quantity":50,"unit":"pcs"},{"sku":"SKU-2","name":"Pasta","quantity":120,"unit":"pcs"}]}
```

### 4. Send to Central via Kafka
```bash
curl -sS -X POST "http://localhost:8081/warehouse/send?format=json"
```
**Output:**
```text
SENT warehouseId=linz topic=warehouse.linz.stock payloadType=application/json
```

### 5. Check Acknowledgement
```bash
curl -sS "http://localhost:8081/warehouse/last-ack"
```
**Output:**
```text
warehouseId=linz lastAck=SUCCESS
```

### 6. Check Central Aggregation
```bash
curl -sS -H "Accept: application/json" "http://localhost:8080/central/stocks"
```
**Output:**
```json
{"generatedAt":"...","warehouses":[{"warehouseId":"linz","timestamp":"2026-01-20T08:00:00Z","items":[{"sku":"SKU-1","name":"Soap","quantity":50,"unit":"pcs"},{"sku":"SKU-2","name":"Pasta","quantity":120,"unit":"pcs"}]}]}
```

### 7. Check Logs
You can verify the message flow in the logs:

```bash
tail -n 50 logs/central.log
tail -n 50 logs/warehouse-linz.log
```

**Expected Log Output ( Warehouse):**
```text
SEND topic=warehouse.linz.stock payloadType=application/json payload={...}
RECV_ACK topic=warehouse.ack key=linz value=SUCCESS
```

**Expected Log Output (Central):**
```text
RECV topic=warehouse.linz.stock key=linz payloadType=application/json payload={...}
SEND_ACK topic=warehouse.ack key=linz value=SUCCESS
```
