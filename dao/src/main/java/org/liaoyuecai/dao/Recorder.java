import java.util.List;

public abstract class Recorder {

    final static String TRANSACTION_TABLE = "mq_transaction";
    final static String TRANSACTION_OPERATION_TABLE = "mq_transaction_operation";
    final static String TRANSACTION_FAIL_TABLE = "mq_transaction_fail";

    abstract void transactionLog(Transaction transaction);

    abstract void operationLog(TransactionMessage message);

    abstract void updateOperationStatus(TransactionMessage message);

    abstract void updateTransactionStatus(TransactionMessage message);

    abstract List<Transaction> getUnfinishedTransaction();
}
