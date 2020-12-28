package com.parser.parser.manager;

public interface ProducerUrl extends Runnable {
    void put(String url);
}
