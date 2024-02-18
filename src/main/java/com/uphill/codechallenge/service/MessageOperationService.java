package com.uphill.codechallenge.service;

import com.uphill.codechallenge.model.MessageResponse;

public interface MessageOperationService {
    MessageResponse handleMessage(String msg);

    MessageResponse endMessage();
}
