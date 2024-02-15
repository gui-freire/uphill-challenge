package com.uphill.codechallenge.service;

import com.uphill.codechallenge.gateway.MessageGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.connection.TcpConnectionExceptionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@MessageEndpoint
public class MessageHandlerServiceImpl implements MessageHandlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerServiceImpl.class);

    private final MessageGateway gateway;

    private final MessageOperationService messageOperationService;
    private final UUID uuid;

    private String connectionId;

    public MessageHandlerServiceImpl(MessageGateway gateway, MessageOperationService messageOperationService) {
        this.gateway = gateway;
        this.messageOperationService = messageOperationService;
        uuid = UUID.randomUUID();
    }

    @EventListener(TcpConnectionOpenEvent.class)
    @Override
    public void sendGreetingMessage(TcpConnectionOpenEvent event) {
        connectionId = event.getConnectionId();
        gateway.send("HI, I AM " + uuid, connectionId);
    }

    @ServiceActivator(inputChannel = "inboundChannel")
    @Override
    public void handleIncomingMsg(byte[] msg) {
        String stringMsg = new String(msg);
        LOGGER.info("Entered handleIncomingMsg with message {}", stringMsg);
        gateway.send(messageOperationService.handleMessage(stringMsg), connectionId);
    }

    @EventListener(TcpConnectionExceptionEvent.class)
    @Override
    public void handleTimeout(TcpConnectionExceptionEvent event) {
        LOGGER.debug("Timeout exception");
        gateway.send(messageOperationService.endMessage(), connectionId);
    }
}
