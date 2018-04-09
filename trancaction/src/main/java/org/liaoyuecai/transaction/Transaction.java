package org.liaoyuecai.transaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {
    public static final int NON_EXECUTION = 0;
    public static final int EXECUTING = 1;
    public static final int SUCCESS = 2;
    public static final int FAIL = 3;
    String id = UUID.randomUUID().toString();
    volatile List<TransactionOperation> operations = new ArrayList();
    int status = NON_EXECUTION;
    String name;

    public Transaction(String name) {
        this.name = name;
    }

    public Transaction addOperation(String queue, String operation, Object params) {
        operations.add(new TransactionOperation(id, operations.size() + 1, queue, operation, JSON.toJSONString(params)));
        return this;
    }

    public Transaction addOperation(String queue, String operation, Object params, String name) {
        operations.add(new TransactionOperation(id, operations.size() + 1, queue, name, operation, JSON.toJSONString(params)));
        return this;
    }

    public String getCurrentOperation() {
        return operations.get(0).operation;
    }

    public <T> T getParams(Class<T> clazz) {
        return JSONObject.parseObject(operations.get(0).params, clazz);
    }

    void complete() {
        operations.get(0).success();
        operations.remove(0);
        if (operations.isEmpty())
            status = SUCCESS;
    }

    void defeated() {
        operations.get(0).fail();
        status = FAIL;
    }

    public String getId() {
        return id;
    }

    public List<TransactionOperation> getOperations() {
        return operations;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}
