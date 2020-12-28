package com.parser.parser.manager;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Component
public class ProducerUrlImpl implements ProducerUrl {
    private BlockingQueue<String> queue;
    private final int SIZE = 50;
    private UrlQueue urlQueue;
    private String https = "https://";
    private String http = "http://";
    private String www = "wwww";

    @Autowired
    public ProducerUrlImpl(UrlQueue urlQueue) {
        this.queue = urlQueue.getQueue();
    }

    @SneakyThrows
    public void run() {
        try {
                if (queue.size() >= SIZE) {
                    System.out.println(queue.size());
                    queue.wait(100);
                }
//            while (true) {
//                if (queue.size() != SIZE) {
////                    queue.notify();
//                } else {
//                    queue.wait();
//                }
//            }
        } catch (InterruptedException ex) {
            queue.wait();
        }
    }

    @SneakyThrows
    @Override
    @Async("threadPoolTaskExecutorProducer")
    public void put(String url) {
        try {
            System.out.println("Produced resource - Queue size now = " + queue.size() + " tread : "+ Thread.currentThread().getName());
            if (url.startsWith(www)) {
                url = https + url.substring(4);
            }
            queue.put(url);
            try {
                Document doc = Jsoup.connect(url).get();
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String urlText = link.attr("href");
                    if (urlText.startsWith(www)) {
                        urlText = https + url.substring(4);
                    }
                    if (urlText.startsWith(https) || urlText.startsWith(http)) {
                        queue.put(urlText);
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Bad url" + url);
            }
            Thread.sleep(100); // simulate time passing during read
        } catch (InterruptedException ex) {
            queue.wait();
        }
    }
}
