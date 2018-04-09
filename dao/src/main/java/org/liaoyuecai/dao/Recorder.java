package org.liaoyuecai.dao;

import org.liaoyuecai.transaction.Transaction;
import org.liaoyuecai.transaction.TransactionOperation;

import java.util.List;

public abstract class Recorder {

    public final static String TRANSACTION_TABLE = "mq_transaction";
    public final static String TRANSACTION_OPERATION_TABLE = "mq_transaction_operation";
    public final static String TRANSACTION_FAIL_TABLE = "mq_transaction_fail";

    public abstract void transactionLog(Transaction transaction);

    public abstract void updateOperationStatus(TransactionOperation operation);

    public abstract void transactionFail(String transactionId,Exception e);

    public abstract void updateTransactionStatus(Transaction transaction);

    public abstract List<Transaction> getUnfinishedTransaction();

    public abstract List<Transaction> getFailTransaction();
}
