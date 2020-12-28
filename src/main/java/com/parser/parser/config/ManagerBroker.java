package com.parser.parser.config;

import com.parser.parser.manager.ConsumerModels;
import com.parser.parser.manager.ConsumerUrl;
import com.parser.parser.manager.ProducerUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ManagerBroker {
    private ProducerUrl producerUrl;
    private ConsumerUrl consumerUrl;
    private ConsumerModels consumerModels;
    private int processors = Runtime.getRuntime().availableProcessors() / 2 + 1;

    @Autowired
    public ManagerBroker(ProducerUrl producerUrl, ConsumerUrl consumerUrl, ConsumerModels consumerModels) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.newThread(producerUrl).start();
        executor.newThread(consumerUrl).start();
        executor.newThread(consumerModels).start();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors + 1);
        executor.initialize();
//        new Thread(producerUrl).start();
//        new Thread(consumerUrl).start();
    }
}
