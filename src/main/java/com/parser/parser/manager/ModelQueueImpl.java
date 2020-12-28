package com.parser.parser.manager;

import com.parser.parser.model.ResponseModel;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class ModelQueueImpl implements ModelQueue {
    private BlockingQueue<ResponseModel> queue;
    private final int SIZE;

    public ModelQueueImpl() {
        this.SIZE = 50;
        this.queue = new LinkedBlockingDeque(SIZE);
    }

    @Override
    public void put(ResponseModel url) {
        try {
            queue.put(url);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseModel take() {
        return queue.remove();
    }

    @Override
    public BlockingQueue getQueue() {
        return queue;
    }
}
