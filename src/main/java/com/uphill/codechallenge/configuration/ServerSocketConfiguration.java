package com.uphill.codechallenge.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class ServerSocketConfiguration {

    @Bean
    public MessageChannel inboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel outboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public AbstractServerConnectionFactory serverConnectionFactory() {
        TcpNetServerConnectionFactory tcpNetServerConnectionFactory = new TcpNetServerConnectionFactory(12345);
        tcpNetServerConnectionFactory.setSoTimeout(30000);
//        tcpNetServerConnectionFactory.setSingleUse(true);
        return tcpNetServerConnectionFactory;
    }

    @Bean
    public TcpReceivingChannelAdapter receivingChannelAdapter(AbstractServerConnectionFactory serverConnectionFactory, MessageChannel inboundChannel) {
        TcpReceivingChannelAdapter tcpReceivingChannelAdapter = new TcpReceivingChannelAdapter();
        tcpReceivingChannelAdapter.setConnectionFactory(serverConnectionFactory);
        tcpReceivingChannelAdapter.setOutputChannel(inboundChannel);
        return tcpReceivingChannelAdapter;
    }

    // Outbound channel adapter
    @Bean
    @ServiceActivator(inputChannel = "outboundChannel")
    public TcpSendingMessageHandler tcpSendingMessageHandler(AbstractServerConnectionFactory serverConnectionFactory) {
        TcpSendingMessageHandler tcpSendingMessageHandler = new TcpSendingMessageHandler();
        tcpSendingMessageHandler.setConnectionFactory(serverConnectionFactory);
        return tcpSendingMessageHandler;
    }
}
