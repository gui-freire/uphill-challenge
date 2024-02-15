package com.uphill.codechallenge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
//@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MessageOperationServiceImpl implements MessageOperationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageOperationServiceImpl.class);
    private static final String GREETING_MSG = "HI %s";
    private static final String GOODBYE_MSG = "BYE %s, WE SPOKE FOR %d MS";

    private String name;

    private long startTime;

    private final NodeService nodeService;

    public MessageOperationServiceImpl(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public String handleMessage(String msg) {
        if (isGreeting(msg)) {
            startTime = System.currentTimeMillis();
            Matcher matcher = createMatcher("[a-zA-Z0-9-]+$", msg);
            if (matcher.find()) {
                name = matcher.group();
                return String.format(GREETING_MSG, name);
            }
            return "";
        } else if (isGoodbye(msg)) {
            return endMessage();
        } else if (isNodeOperation(msg)){
            String[] split = msg.split("\\s+");
            if (isAdd(msg)) {
                return nodeService.addNode(split[2]);
            } else if (isRemove(msg)) {
                return nodeService.removeNode(split[2]);
            }
            return null;
        } else if (isEdgeOperation(msg)) {
            String[] split = msg.split("\\s+");
            String initialNode = split[2];
            String finalNode = split[3];
                if (isAdd(msg)) {
                    return nodeService.addEdge(initialNode, finalNode, Integer.parseInt(split[4]));
                } else if (isRemove(msg)) {
                    return nodeService.removeEdge(initialNode, finalNode);
                }
            return null;
        } else if (isShortestPath(msg)) {
            String[] split = msg.split("\\s+");
            String initialNode = split[2];
            String finalNode = split[3];
            return nodeService.shortestPath(initialNode, finalNode);
        } else if (isCloserThan(msg)) {
            String[] split = msg.split("\\s+");
            String node = split[3];
            String weight = split[2];
            return nodeService.closerThan(node, Integer.parseInt(weight));
        }
        else {
            return "SORRY, I DID NOT UNDERSTAND THAT";
        }
    }

    @Override
    public String endMessage() {
        long totalTime = System.currentTimeMillis() - startTime;
        return String.format(GOODBYE_MSG, name, totalTime);
    }

    private boolean isGreeting(String msg) {
        return msg.contains("HI, I AM");
    }

    private boolean isGoodbye(String msg) {
        return msg.contains("BYE MATE!");
    }

    private boolean isNodeOperation(String msg) {
        return msg.contains("NODE");
    }

    private boolean isAdd(String msg) {
        return msg.contains("ADD");
    }

    private boolean isRemove(String msg) {
        return msg.contains("REMOVE");
    }

    private boolean isEdgeOperation(String msg) {
        return msg.contains("EDGE");
    }

    private boolean isShortestPath(String msg) {
        return msg.contains("SHORTEST PATH");
    }

    private boolean isCloserThan(String msg) {
        return msg.contains("CLOSER THAN");
    }

    private Matcher createMatcher(String regex, String msg) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(msg);
    }
}
