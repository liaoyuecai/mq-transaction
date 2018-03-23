package org.liaoyuecai.transaction;

public class Transaction {
    static final int NON_EXECUTION = 0;
    static final int EXECUTING = 1;
    static final int SUCCESS = 2;
    static final int FAIL = 3;
    String id = UUID.randomUUID().toString();
    List<TransactionMessage> execute = new ArrayList();
    int status = NON_EXECUTION;
    String name;

    public Transaction(String name) {
        this.name = name;
    }

    public Transaction addOperation(String queue, String operation, Object params) {
        execute.add(new TransactionMessage(queue, operation, JSON.toJSONString(params)));
        return this;
    }

    public Transaction addOperation(String queue, String operation, Object params, String name) {
        execute.add(new TransactionMessage(queue, name, operation, JSON.toJSONString(params)));
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
            status = SUCCESS;
    }

    void defeated(){
        execute.get(0).fail();
        status = FAIL;
    }
}
