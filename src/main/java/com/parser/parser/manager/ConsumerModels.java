package com.parser.parser.manager;

import com.parser.parser.model.ResponseFullModel;

public interface ConsumerModels extends Runnable {

    Iterable<ResponseFullModel> getResponse();


    boolean getFlag();
}
