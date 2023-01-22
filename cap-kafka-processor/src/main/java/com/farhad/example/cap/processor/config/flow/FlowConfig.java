package com.farhad.example.cap.processor.config.flow;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FlowConfig {
    

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.topic.request}")
    private String requestTopic;

    @Value("${kafka.topic.reply}")
    private String replyTopic;

    @Value("${kafka.request-reply.timeout-ms}")
    private Long replyTimeout;

    private TaskExecutor kafkaConsumerThreadPool() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("kafka-cap-consumer-channel-thread-pool");
        executor.initialize();
        return executor ;
    }

    private TaskExecutor countWordsThreadPool() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("cap-channel-thread-pool");
        executor.initialize();
        return executor ;
    }


    @Bean(name = "capKafkaConsumerChannel")
    public MessageChannel getKafkaConsumerChannell() {

        return MessageChannels.executor("capKafkaConsumerChannel",kafkaConsumerThreadPool()).get();
    }

    @Bean(name = "capChannel")
    public MessageChannel getCountWordsChannel() {

        return MessageChannels.executor("capChannel",countWordsThreadPool()).get();
    }
    
    @Bean
    public IntegrationFlow capInboundGateFlow(
                @Qualifier("capKafkaListenerContainerFactory") KafkaMessageListenerContainer<String, String> kafkaListenerContainerFactory,
                @Qualifier("capReplyTemplate") KafkaTemplate<String, String> replyTemplate) {

        return IntegrationFlows
                    .from(
                       Kafka
                        .inboundGateway(kafkaListenerContainerFactory, replyTemplate)
                        .replyTimeout(30_000)       
                    )
                    .transform(String.class , source -> StringUtils.capitalize(source))
                    .logAndReply(m ->{
                         log.info("cap(Processor): {} - Header: {}",m.getPayload(), m.getHeaders());
                         return m;
                        });
    }


}
