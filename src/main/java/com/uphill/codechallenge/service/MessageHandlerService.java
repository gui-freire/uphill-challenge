package com.uphill.codechallenge.service;

import org.springframework.integration.ip.tcp.connection.TcpConnectionExceptionEvent;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;

public interface MessageHandlerService {

    void sendGreetingMessage(TcpConnectionOpenEvent event);

    void handleTimeout(TcpConnectionExceptionEvent event);

    void handleIncomingMsg(byte[] msg);
}
