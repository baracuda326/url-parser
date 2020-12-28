package com.parser.parser.manager;

import java.util.concurrent.BlockingQueue;

public interface UrlQueue {
    void put(String url);

    String take();

    BlockingQueue getQueue();
}
