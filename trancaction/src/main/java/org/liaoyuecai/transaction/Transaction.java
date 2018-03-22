package org.liaoyuecai.transaction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    List<TransactionMessage> execute = new ArrayList();
    int status = 0;
    public Transaction addOperation(String queue, String operation, Object params) {
        execute.add(new TransactionMessage(queue, operation, JSON.toJSONString(params)));
        return this;
    }

    public Transaction addOperation(String queue, String operation, Object params, String remark) {
        execute.add(new TransactionMessage(queue, remark, operation, JSON.toJSONString(params)));
        return this;
    }

    public String getCurrentOperation() {
        return execute.get(0).operation;
    }

    public <T> T getParams(Class<T> clazz) {
        return JSONObject.parseObject(execute.get(0).params, clazz);
    }

    void complete(){
        execute.get(0).success();
        execute.remove(0);
        if (execute.isEmpty())
            status = 1;
    }

    void defeated(){
        execute.get(0).fail();
    }
}
