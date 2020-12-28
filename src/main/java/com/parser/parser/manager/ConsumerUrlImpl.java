package com.parser.parser.manager;

import com.parser.parser.model.ResponseModel;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Component
public class ConsumerUrlImpl implements ConsumerUrl {
    private BlockingQueue<String> queue;
    private BlockingQueue<ResponseModel> queueModel;
    private UrlQueue urlQueue;
    private ModelQueue modelQueue;

    @Autowired
    public ConsumerUrlImpl(UrlQueue urlQueue, ModelQueue modelQueue) {
        this.queue = urlQueue.getQueue();
        this.queueModel = modelQueue.getQueue();
    }

    @SneakyThrows
    public void run() {
        while (true) {
            try {
                String url = queue.take();
                System.out.println("Consumed resource - Queue size now = " + queue.size());
                take(url);
            } catch (InterruptedException ex) {
                System.out.println("CONSUMER INTERRUPTED");
            }
        }
    }

    @Async("threadPoolTaskExecutorConsumer")
    void take(String url) {
        try {
            Thread.sleep(100); // simulate time passing
        } catch (InterruptedException ex) {
            System.out.println("Consumer Read INTERRUPTED");
        }
        System.out.println("Consuming object " + url + " thread : " + Thread.currentThread().getName());

        try {
            queueModel.put(getTextFromUrl(url));
        } catch (InterruptedException e) {
            System.out.println("Consumer Read INTERRUPTED");
        }
    }

    private ResponseModel getTextFromUrl(String url) {
        ResponseModel responseModel;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements textArticle = doc.select("p");
            responseModel = ResponseModel.builder()
                    .url(url)
                    .urlText(textArticle.text())
                    .build();
        } catch (IOException e) {
            throw new IllegalArgumentException("Bad url" + url);
        }
        return responseModel;
    }
}
