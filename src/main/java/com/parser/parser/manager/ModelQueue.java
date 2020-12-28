package com.parser.parser.manager;

import com.parser.parser.model.ResponseModel;

import java.util.concurrent.BlockingQueue;

public interface ModelQueue {
    void put(ResponseModel url);

    ResponseModel take();

    BlockingQueue getQueue();
}
