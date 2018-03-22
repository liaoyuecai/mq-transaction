package org.liaoyuecai.transaction;

import java.io.Serializable;
import java.util.UUID;

class TransactionMessage<T> implements Serializable{
    String id = UUID.randomUUID().toString();
    String queue;
    String remark;
    String operation;
    String params;
    int status = 0;

    TransactionMessage(String queue, String operation, String params) {
        this.queue = queue;
        this.operation = operation;
        this.params = params;
    }

    TransactionMessage(String queue, String remark, String operation, String params) {
        this.queue = queue;
        this.remark = remark;
        this.operation = operation;
        this.params = params;
    }

    void success(){
        status = 1;
    }

    void fail(){
        status = 2;
    }

}
