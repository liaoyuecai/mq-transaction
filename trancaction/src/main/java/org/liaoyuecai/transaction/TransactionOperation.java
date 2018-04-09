package org.liaoyuecai.transaction;

import java.io.Serializable;
import java.util.UUID;

public class TransactionOperation<T> implements Serializable{
    String id = UUID.randomUUID().toString();
    int no;
    String transactionId;
    String queue;
    String name;
    String operation;
    String params;
    int status = Transaction.NON_EXECUTION;

    TransactionOperation(String transactionId,int no,String queue, String operation, String params) {
        this.transactionId = transactionId;
        this.no = no;
        this.queue = queue;
        this.operation = operation;
        this.params = params;
    }

    TransactionOperation(String transactionId,int no,String queue, String name, String operation, String params) {
        this.transactionId = transactionId;
        this.no = no;
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

    void execute(){
        status = Transaction.EXECUTING;
    }

    public String getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getQueue() {
        return queue;
    }

    public String getName() {
        return name;
    }

    public String getOperation() {
        return operation;
    }

    public String getParams() {
        return params;
    }

    public int getStatus() {
        return status;
    }

    public int getNo() {
        return no;
    }
}
