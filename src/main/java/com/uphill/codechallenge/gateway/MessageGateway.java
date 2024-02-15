package com.uphill.codechallenge.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.ip.IpHeaders;

@MessagingGateway(defaultRequestChannel = "outboundChannel")
public interface MessageGateway {

    @Gateway(payloadExpression = "#args[0]",
            headers = @GatewayHeader(name = IpHeaders.CONNECTION_ID, expression = "#args[1]"))
    void send(String message, String cid);
}
