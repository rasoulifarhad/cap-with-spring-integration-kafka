package com.farhad.example.cap.processor.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {
    

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

   

    @Value("${kafka.request-reply.timeout-ms}")
    private Long replyTimeout;

    @Value("${kafka.topic.request}")
    private String requestTopic;

    @Value("${kafka.topic.reply}")
    private String replyTopic;

    
    @Bean
    public Map<String, Object> capConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        // props.put(JsonDeserializer.TYPE_MAPPINGS, "lightMeasuredPayload:com.example.kafka.streetlights.producer.model.LightMeasuredPayload" );
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.farhad.example.*");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "cap"+groupId);
        return props;
    }

        @Bean
    public Map<String, Object> capProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.farhad.example.*");
        // props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.farhad.example.springintegration");

        // props.put(JsonSerializer.TYPE_MAPPINGS,
        // "lightMeasuredPayload:com.example.kafka.streetlights.consumer.model.LightMeasuredPayload");
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }


    @Bean("capRequestConsumerFactory")
    public ConsumerFactory<String, String> requestConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(capConsumerConfigs(), new StringDeserializer(),
            new StringDeserializer());
    }

    @Bean("capKafkaListenerContainerFactory")
    public KafkaMessageListenerContainer<String, String> kafkaListenerContainerFactory() {

        ContainerProperties containerProperties = new ContainerProperties(requestTopic);
        return new KafkaMessageListenerContainer<>(requestConsumerFactory(), containerProperties);	
    }

    

    @Bean("capReplyProducerFactory")
    public ProducerFactory<String, String> replyProducerFactory() {
        return new DefaultKafkaProducerFactory<>(capProducerConfigs());
    }

    @Bean("capReplyTemplate")
    public KafkaTemplate<String, String> replyTemplate() {
        return new KafkaTemplate<>(replyProducerFactory());
    }
    
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }


    @Bean
    public NewTopic replyTopic() {
        return TopicBuilder
                .name(requestTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic requestTopic() {
        return TopicBuilder
                .name(requestTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
   

}
