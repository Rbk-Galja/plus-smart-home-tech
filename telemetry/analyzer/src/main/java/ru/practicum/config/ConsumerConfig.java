package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "analyzer.kafka")
@Getter
@Setter
public class ConsumerConfig {
    private String bootstrapServers;
    private String hubValueDeserializer;
    private String snapshotValueDeserializer;
    private String groupHubEvents;
    private String groupSnapshots;
    private int pollTimeoutMs;

    @Bean
    public Consumer<String, HubEventAvro> hubEventsConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", hubValueDeserializer);
        props.put("group.id", groupHubEvents);
        props.put("enable.auto.commit", "true");
        props.put("max.poll.records", 200);
        return new KafkaConsumer<>(props);
    }

    @Bean
    public Consumer<String, SensorsSnapshotAvro> snapshotsConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", snapshotValueDeserializer);
        props.put("group.id", groupSnapshots);
        props.put("enable.auto.commit", "false");
        props.put("max.poll.records", 50);
        return new KafkaConsumer<>(props);
    }
}
