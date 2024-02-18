package com.uphill.codechallenge.service;

import com.uphill.codechallenge.enums.MessagesEnum;
import com.uphill.codechallenge.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageOperationServiceImpl implements MessageOperationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageOperationServiceImpl.class);
    private static final String GREETING_MSG = "HI %s";
    private static final String GOODBYE_MSG = "BYE %s, WE SPOKE FOR %d MS";

    private String name;

    private final String connectionId;

    private long startTime;

    private final NodeService nodeService;

    public MessageOperationServiceImpl(String client, ApplicationContext context) {
        this.connectionId = client;
        this.nodeService = context.getBean(NodeService.class);
    }

    @Override
    public MessageResponse handleMessage(String msg) {
        MessageResponse response = new MessageResponse(connectionId);
        MessagesEnum messagesEnum = MessagesEnum.findValue(msg);
        String[] split = msg.split("\\s+");;
        String initialNode = "";
        String finalNode = "";
        Integer weight = 0;
        switch (messagesEnum) {
            case GREETING:
                startTime = System.currentTimeMillis();
                Matcher matcher = createMatcher("[a-zA-Z0-9-]+$", msg);
                if (matcher.find()) {
                    name = matcher.group();
                    response.setMessage(String.format(GREETING_MSG, name));
                }
                break;
            case GOODBYE:
                return endMessage();
            case ADD_NODE:
                response.setMessage(nodeService.addNode(split[2]));
                break;
            case ADD_EDGE:
                initialNode = split[2];
                finalNode = split[3];
                weight = Integer.parseInt(split[4]);
                response.setMessage(nodeService.addEdge(initialNode, finalNode, weight));
                break;
            case REMOVE_NODE:
                response.setMessage(nodeService.removeNode(split[2]));
                break;
            case REMOVE_EDGE:
                initialNode = split[2];
                finalNode = split[3];
                response.setMessage(nodeService.removeEdge(initialNode, finalNode));
                break;
            case CLOSER_THAN:
                initialNode = split[3];
                weight = Integer.valueOf(split[2]);
                response.setMessage(nodeService.closerThan(initialNode, weight));
                break;
            case SHORTEST_PATH:
                initialNode = split[2];
                finalNode = split[3];
                response.setMessage(nodeService.shortestPath(initialNode, finalNode));
                break;
            default:
                response.setMessage("SORRY, I DID NOT UNDERSTAND THAT");
        }
        return response;
    }

    @Override
    public MessageResponse endMessage() {
        MessageResponse response = new MessageResponse(connectionId);
        long totalTime = System.currentTimeMillis() - startTime;
        response.setMessage(String.format(GOODBYE_MSG, name, totalTime));
        return response;
    }

    private Matcher createMatcher(String regex, String msg) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(msg);
    }
}
