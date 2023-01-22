package com.farhad.example.cap.edge.config.flow;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class FlowConfig {
    

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${kafka.topic.request}")
    private String capRequestTopic;

    @Value("${kafka.request-reply.timeout-ms}")
    private Long replyTimeout;

    @Value("${kafka.topic.reply}")
    private String capReplyTopic;


    private TaskExecutor receiveTextThreadPool() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("receive-text-channel-thread-pool");
        executor.initialize();
        return executor ;
    }

    @Bean(name = "capReceiveTextChannel")
    public MessageChannel getReceiveTextChannel() {

        return MessageChannels.executor("capReceiveTextChannel",receiveTextThreadPool()).get();
    }

    // @Bean
    // public IntegrationFlow capOutboundGateFlow(@Qualifier( "capReplyKafkaTemplate") ReplyingKafkaTemplate<String, String ,String> template,
    //                     @Qualifier("capRequestProducerFactory") ProducerFactory<String, String> requestProducerFactory,
    //                     ConcurrentKafkaListenerContainerFactory<String, String> factory) {

    //     ConcurrentMessageListenerContainer<String, String> replyContainer = factory.createContainer(capReplyTopic);
    //     replyContainer.getContainerProperties().setMissingTopicsFatal(false);
    //     replyContainer.getContainerProperties().setGroupId("cap" + groupId);

    //     return IntegrationFlows.from(getReceiveTextChannel())
    //                            .log() 
    //                            .handle(Kafka
    //                                      .outboundGateway(requestProducerFactory,replyContainer)
    //                                      .configureKafkaTemplate(t -> t.defaultReplyTimeout(Duration.ofSeconds(30)))
    //                             )
    //                             .channel("kafkaReplies")
    //                             .logAndReply()
    //                             // .get()
    //                             ;
    // }


    public interface CapFunction extends Function<String,String>  {} 

    @Bean
    public IntegrationFlow capOutGate(@Qualifier( "capReplyKafkaTemplate") ReplyingKafkaTemplate<String, String ,String> template) {
        return IntegrationFlows.from(CapFunction.class,
                                            gateway -> gateway.beanName("catOutGate"))
                                .handle(Kafka
                                    .outboundGateway(template)
                                    .topic(capRequestTopic))
                                .logAndReply();
               }
    @Bean
    public IntegrationFlow capOutboundGateFlow(@Qualifier( "capReplyKafkaTemplate") ReplyingKafkaTemplate<String, String ,String> template) {

        return IntegrationFlows.from(getReceiveTextChannel())
                               .log() 
                               .handle(Kafka
                                         .outboundGateway(template)
                                         .topic(capRequestTopic)
                                         
                                        //  .configureKafkaTemplate(t -> t.defaultReplyTimeout(Duration.ofSeconds(30)))
                                )
                                .channel("kafkaReplies")    
                                .logAndReply()
                                // .get()
                                ;
    }

   
}
