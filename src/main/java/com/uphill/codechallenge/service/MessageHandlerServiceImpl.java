package com.uphill.codechallenge.service;

import com.uphill.codechallenge.gateway.MessageGateway;
import com.uphill.codechallenge.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ip.tcp.connection.TcpConnectionExceptionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@MessageEndpoint
public class MessageHandlerServiceImpl implements MessageHandlerService{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerServiceImpl.class);

    private final ApplicationContext context;
    private final MessageGateway gateway;
    private final UUID uuid;

    private Map<String, MessageOperationService> messageOperationService;


    public MessageHandlerServiceImpl(MessageGateway gateway, ApplicationContext context) {
        this.context = context;
        this.gateway = gateway;
        uuid = UUID.randomUUID();
        this.messageOperationService = new HashMap<>();
    }

    @EventListener(TcpConnectionOpenEvent.class)
    @Override
    public void sendGreetingMessage(TcpConnectionOpenEvent event) {
        String connectionId = event.getConnectionId();
        if (!messageOperationService.containsKey(connectionId)) {
            messageOperationService.put(connectionId, new MessageOperationServiceImpl(connectionId, context));
        }
        gateway.send("HI, I AM " + uuid, connectionId);
    }

    @ServiceActivator(inputChannel = "inboundChannel")
    @Override
    public void handleIncomingMsg(Message message) {
        String stringMsg = new String((byte[]) message.getPayload());
        String connectionId = message.getHeaders().get("ip_connectionId").toString();
        LOGGER.debug("Entered handleIncomingMsg with message {} from {}", stringMsg, connectionId);
        MessageOperationService service = this.messageOperationService.get(connectionId);
        MessageResponse response = service.handleMessage(stringMsg);
        gateway.send(response.getMessage(), connectionId);
    }

    @EventListener(TcpConnectionExceptionEvent.class)
    @Override
    public void handleTimeout(TcpConnectionExceptionEvent event) {
        LOGGER.debug("Timeout exception");
        MessageOperationService service = this.messageOperationService.get(event.getConnectionId());
        MessageResponse response = service.endMessage();
        gateway.send(response.getMessage(), response.getClientId());
        this.messageOperationService.remove(event.getConnectionId(), service);
    }
}
