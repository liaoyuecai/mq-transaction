package org.liaoyuecai.transaction;

import java.io.Serializable;
import java.util.UUID;

class TransactionMessage<T> implements Serializable{
    String id = UUID.randomUUID().toString();
    String queue;
    String name;
    String operation;
    String params;
    int status = 0;

    TransactionMessage(String queue, String operation, String params) {
        this.queue = queue;
        this.operation = operation;
        this.params = params;
    }

    TransactionMessage(String queue, String name, String operation, String params) {
        this.queue = queue;
        this.name = name;
        this.operation = operation;
        this.params = params;
    }

    void success(){
        status = Transaction.SUCCESS;
    }

    void fail(){
        status = Transaction.FAIL;
    }

    void excute(){
        status = Transaction.EXECUTING;
    }

}
