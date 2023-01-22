package com.farhad.example.cap.edge.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface CapitalizeService {
    // @Gateway(requestChannel = "capReceiveTextChannel", replyChannel = "replyChannel",
    //       headers = {@GatewayHeader(name = "kafka_topic", value ="requestTopic")})
    @Gateway(requestChannel = "capReceiveTextChannel")
    public String capitalize(String request) ;
}
