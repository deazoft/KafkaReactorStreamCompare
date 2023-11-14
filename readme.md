# KafkaReactorCompare

Welcome to **KafkaReactorCompare** – a comprehensive benchmarking suite designed to provide an empirical comparison between **Apache Kafka Streams** and **Project Reactor** for stream processing in the Java ecosystem.

In an era where real-time data processing is not just a luxury but a necessity, the choice of the right stream processing framework becomes crucial. **KafkaReactorCompare** aims to demystify the performance nuances of Kafka Streams and Project Reactor, providing data-driven insights to developers, architects, and technology enthusiasts.

## 🚀 Project Overview

**KafkaReactorCompare** harnesses a Docker-based environment to ensure uniform and reproducible benchmarking conditions. Through the integration of **Prometheus** for metrics gathering and **Grafana** for data visualization, this project offers a granular view into the performance characteristics under controlled scenarios.

Key Highlights:

- **Docker-Compose Orchestration**: Ensuring consistent benchmarking setup including Kafka, Zookeeper, Prometheus, Grafana, and the Java applications.
- **Java Application Containers**: Separate Docker containers for a Kafka Producer, a Kafka Streams Consumer, and a Project Reactor Kafka Consumer, facilitating isolated and consistent testing environments.
- **Automated Benchmarking Scripts**: Simplify the execution process with scripts managing the startup, event publication, and consumption.
- **Real-Time Monitoring**: Utilizing Prometheus for data collection and Grafana for an intuitive, live data visualization experience.

## 🎯 Goals of KafkaReactorCompare

- **Performance Clarity**: Deliver definitive, side-by-side performance metrics like throughput, latency, and resource usage.
- **User-Friendly**: Accessible and easy-to-use setup, inviting exploration and experimentation.
- **Reproducibility**: Enable users to replicate benchmarks, ensuring validation of results and independent experimentation.