# DEZSYS_GK73_WAREHOUSE_MOM – Protokoll (Vertiefung)

## Architektur (Kurz)
- Apache Kafka als zentrale MOM-Infrastruktur (Broker).
- Pro Warehouse eine "Queue" als Kafka Topic: `warehouse.<id>.stock`.
- Rückmeldung als "Topic" (Publish/Subscribe): `warehouse.ack` (Key = warehouseId, Value = SUCCESS).
- Zentrale konsumiert alle Warehouse-Topics per Pattern und stellt aggregierte Daten per REST (JSON/XML) bereit.

Kafka Grundprinzipien (Topics, Producer/Consumer, CLI) sind in der Kafka Quickstart Doku beschrieben. :contentReference[oaicite:5]{index=5}
`@KafkaListener` / topicPattern siehe Spring Kafka Referenz. :contentReference[oaicite:6]{index=6}

## 1) Nennen Sie mindestens 4 Eigenschaften der Message Oriented Middleware (MOM)
1. Asynchrone Kommunikation: Sender und Empfänger müssen nicht gleichzeitig verfügbar sein.
2. Entkopplung in Zeit und Raum: Kommunikation über Broker/Queue statt direkte Punkt-zu-Punkt-Verbindung.
3. Pufferung / Queueing: Nachrichten können zwischengespeichert und später verarbeitet werden.
4. Zuverlässigkeit/Delivery-Semantik: je nach Broker z.B. Persistenz, Acknowledgements, Retry.
5. Skalierung/Lastverteilung: mehrere Consumer können Nachrichten parallel verarbeiten (Competing Consumers).
Diese Eigenschaften sind typisch für MOM/JMS-artige Systeme. :contentReference[oaicite:7]{index=7}

## 2) Was versteht man unter einer transienten und synchronen Kommunikation?
- Transient: Nachrichten werden nicht (oder nicht dauerhaft) gespeichert; ist der Empfänger offline, gehen sie verloren (best-effort).
- Synchron: Der Sender blockiert und wartet auf Antwort/Empfang (Request/Reply mit unmittelbarer Kopplung).
Im Gegensatz dazu ist asynchron: Sender sendet und läuft weiter, Antwort (falls vorhanden) kommt später.

## 3) Beschreiben Sie die Funktionsweise einer JMS Queue
JMS Queue ist Point-to-Point:
- Producer sendet Messages an eine Queue (Destination).
- Genau EIN Consumer erhält eine konkrete Message (bei mehreren Consumern konkurrieren sie).
- Broker hält Message bis zur Zustellung/Acknowledge vor.
JMS unterstützt u.a. queuing und publish/subscribe. :contentReference[oaicite:8]{index=8}

## 4) JMS Overview – wichtigste JMS Klassen und Zusammenhang
- ConnectionFactory: Erzeugt Connections zum Broker.
- Connection: Physische/logische Verbindung zum Broker.
- Session: Kontext für das Senden/Empfangen; Transaktionen/Acknowledge-Modus.
- Destination: Queue oder Topic.
- MessageProducer: Sendet Messages zur Destination.
- MessageConsumer / MessageListener: Empfängt Messages (pull oder callback-basiert).
- Message: Payload + Header/Properties (z.B. TextMessage, ObjectMessage, MapMessage).
Grundüberblick JMS siehe Oracle Einführung. :contentReference[oaicite:9]{index=9}

## 5) Funktionsweise eines JMS Topic
Publish/Subscribe:
- Producer veröffentlicht an Topic.
- Jeder Subscriber erhält eine Kopie (Broadcast/Multicast-Semantik).
- Optional: Durable Subscriptions (Subscriber bekommt auch Nachrichten, die während Offline-Zeit publiziert wurden).
JMS unterstützt pub/sub explizit. :contentReference[oaicite:10]{index=10}

## 6) Lose gekoppeltes verteiltes System – Bedeutung, Beispiel, warum "lose"?
Lose Kopplung: Komponenten kennen sich nicht direkt, sind zeitlich/technisch entkoppelt, Schnittstelle ist Nachricht/Vertrag.
Beispiel: Warehouse sendet Lagerstand an Kafka Topic; Zentrale kann später konsumieren und aggregieren.
Warum "lose": kein direkter Aufruf/keine direkte Verbindung nötig; Broker übernimmt Routing/Pufferung.
