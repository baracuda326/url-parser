package com.parser.parser.service;

import com.parser.parser.model.ResponseModel;
import com.parser.parser.model.ResponseFullModel;

import java.util.List;

public interface UrlsService {
    Iterable<ResponseModel> getTextFromUrl(List<String> urls);

    Iterable<ResponseFullModel> getKeyFromUrl(String url);
}
