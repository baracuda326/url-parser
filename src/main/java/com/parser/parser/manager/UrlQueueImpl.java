package com.parser.parser.manager;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class UrlQueueImpl implements UrlQueue {
    private BlockingQueue<String> queue;
    private final int SIZE;

    public UrlQueueImpl() {
        this.SIZE = 50;
        this.queue = new LinkedBlockingDeque(SIZE);
    }

    @Override
    public void put(String url) {
        try {
            queue.put(url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String take() {
        return queue.remove();
    }

    @Override
    public BlockingQueue getQueue() {
        return queue;
    }
}
