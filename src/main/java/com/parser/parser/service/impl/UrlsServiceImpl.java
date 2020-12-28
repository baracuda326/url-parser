package com.parser.parser.service.impl;

import com.parser.parser.model.ResponseModel;
import com.parser.parser.manager.ConsumerModels;
import com.parser.parser.manager.ProducerUrl;
import com.parser.parser.model.ResponseFullModel;
import com.parser.parser.service.UrlsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlsServiceImpl implements UrlsService {
    private ProducerUrl producerUrl;
    private ConsumerModels consumerModels;

    @Autowired
    public UrlsServiceImpl(ProducerUrl producerUrl,ConsumerModels consumerModels) {
        this.consumerModels = consumerModels;
        this.producerUrl = producerUrl;
    }

    @Override
    public Iterable<ResponseModel> getTextFromUrl(List<String> urls) {
        for (String url : urls) {
            producerUrl.put(url);
        }
        return null;
    }

    @Override
    public Iterable<ResponseFullModel> getKeyFromUrl(String url) {
        producerUrl.put(url);
        return consumerModels.getResponse();
    }
}
