package com.parser.parser.manager;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.parser.parser.model.ResponseModel;
import com.parser.parser.model.CategoryKeyWord;
import com.parser.parser.model.CategoryModel;
import com.parser.parser.model.ResponseFullModel;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ConsumerModelsImpl implements ConsumerModels {
    private UrlQueue urlQueue;
    private ModelQueue queue;
    private BlockingQueue<ResponseModel> queueModel;
    private Set<ResponseFullModel> set;
    private Map<String, List<String>> data;
    private AtomicBoolean flag;

    public ConsumerModelsImpl(ModelQueue queue, UrlQueue urlQueue) {
        this.urlQueue = urlQueue;
        this.queueModel = queue.getQueue();
        this.set = new CopyOnWriteArraySet<>();
        flag = new AtomicBoolean();
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            if(!queueModel.isEmpty()) {
                ResponseModel responseModel = queueModel.take();
                System.out.println("Consumed resource - Queue size now = " + queueModel.size());
                take(responseModel);
            }
        }
    }
    @Async("threadPoolTaskExecutorConsumerModel")
    void take(ResponseModel responseModel) {
//        try {
//            Thread.sleep(100); // simulate time passing
//        } catch (InterruptedException ex) {
//            System.out.println("Consumer Read INTERRUPTED");
//        }
//        System.out.println("Consuming object " + responseModel + " thread : " + Thread.currentThread().getName());
        Set<String> keyWords = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            List<String> list = entry.getValue();
            for (String value : list) {
                String text = responseModel.getUrlText().toLowerCase();
                if (text.contains(value.toLowerCase())) {
                    keyWords.add(value);
                    set.add(createResponse(responseModel, entry.getKey(), keyWords));
                }
            }
        }
    }

    private ResponseFullModel createResponse(ResponseModel responseModel, String key, Set<String> keyWords) {
        return ResponseFullModel.builder()
                .response(responseModel)
                .category(CategoryModel.builder()
                        .categoryName(key)
                        .keyWords(CategoryKeyWord.builder()
                                .keyWord(keyWords)
                                .build())
                        .build())
                .build();
    }

    @PostConstruct
    void init() {
        List<String> listStarWars = new ArrayList<>(Arrays.asList("star wars", "Star wars", "starwars", "k84"));
        List<String> listBasketball = new ArrayList<>(Arrays.asList("basketball", "nba", "ncaa", "lebron james", "john stokton",
                "anthony davis"));
        data = new HashMap<>();
        data.put("Star Wars", listStarWars);
        data.put("Basketball", listBasketball);
    }

    @Override
    public Iterable<ResponseFullModel> getResponse() {
        Set<ResponseFullModel> response = new CopyOnWriteArraySet<>();
        ExecutorService es = Executors.newSingleThreadExecutor();
        TimeLimiter timeLimiter = SimpleTimeLimiter.create(es);
        try {
            timeLimiter.callWithTimeout(() -> {
                        while (!urlQueue.getQueue().isEmpty() || !queueModel.isEmpty()) {
                            response.addAll(set);
                        }
                        return response;
                    }
                    , 100L, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException ex) {
            return response;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return response;
    }

    @Override
    public boolean getFlag() {
        return flag.getAndSet(queueModel.isEmpty());
    }
}
