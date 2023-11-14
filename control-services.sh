#!/bin/bash

function start_base_services {
    echo "Starting base services: Kafka, Zookeeper, Prometheus, Grafana..."
    docker-compose up -d zookeeper kafka prometheus grafana control-center
    echo "Base services started."
}

function publish_events {
    echo "Starting event publisher..."
    docker-compose up -d --build producer
    echo "Event publisher started."
}

function start_kafka_streams_consumer {
    echo "Starting Kafka Streams Consumer..."
    docker-compose up -d --build kafka-streams-consumer
    echo "Kafka Streams Consumer started."
}

function start_kafka_reactor_consumer {
    echo "Starting Kafka Reactor Consumer..."
    docker-compose up -d --build kafka-reactor-consumer
    echo "Kafka Reactor Consumer started."
}

function create_base_topic {
    echo "Creating base topic..."
    until docker-compose exec kafka kafka-topics --bootstrap-server kafka:9092 --list; do
        sleep 2
    done
    docker-compose exec kafka kafka-topics --create --topic test-topic --partitions 1 --replication-factor 1 --if-not-exists --bootstrap-server kafka:9092
    echo "Base topic created."
}

function delete_base_topic {
    echo "Deleting base topic..."
    until docker-compose exec kafka kafka-topics --bootstrap-server kafka:9092 --list; do
        sleep 2
    done
    docker-compose exec kafka kafka-topics --delete --topic test-topic --bootstrap-server kafka:9092
    echo "Base topic deleted."
}

function stop_base_services {
    echo "Stopping base services..."
    docker-compose down -d zookeeper kafka prometheus grafana control-center
    echo "Base services stopped."
}

function move_off_set_last {
    group=$1
    echo "Moving offset to last..."
    docker-compose exec kafka kafka-consumer-groups --bootstrap-server kafka:9092 --group $group --reset-offsets --to-latest --all-topics --execute
    echo "Offset moved to last."
}

# Check the first command line argument
case "$1" in
    start-base)
        start_base_services
        ;;
    publish)
        publish_events
        ;;
    consume-streams)
        start_kafka_streams_consumer
        ;;
    create-base-topic)
        create_base_topic    
        ;;
    delete-base-topic)
        delete_base_topic    
        ;;    
    consume-reactor)
        start_kafka_reactor_consumer
        ;;
    stop-base) 
        stop_base_services
        ;;    
    move-off-set-last)
        move_off_set_last "$2"
        ;;
    *)
        echo "Usage: $0 {start-base|publish|consume-streams|consume-reactor|create-base-topic|delete-base-topic|stop-base}"
        exit 1
esac
