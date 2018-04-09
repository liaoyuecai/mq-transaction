package org.liaoyuecai.dao.mysql;

import com.alibaba.fastjson.JSON;
import org.liaoyuecai.dao.Recorder;
import org.liaoyuecai.transaction.Transaction;
import org.liaoyuecai.transaction.TransactionOperation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.*;

public class MySqlRecorder extends Recorder {

    JdbcTemplate template;

    public MySqlRecorder(String dbUrl, String userName, String pwd) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(dbUrl, userName, pwd);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        template = new JdbcTemplate(dataSource);
        databaseCheck();
    }

    public MySqlRecorder(JdbcTemplate template) {
        this.template = template;
        databaseCheck();
    }

    void databaseCheck() {
        StringBuilder sqlBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sqlBuilder.append(Recorder.TRANSACTION_TABLE)
                .append("('id' varchar(64) Primary key,")
                .append("'name' varchar(200),")
                .append("'status' smallint ,")
                .append("'createTime' datetime DEFAULT CURRENT_TIMESTAMP)");
        template.execute(sqlBuilder.toString());
        sqlBuilder.delete(0, sqlBuilder.length());
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS ")
                .append(Recorder.TRANSACTION_OPERATION_TABLE)
                .append("('id' varchar(64) Primary key,")
                .append("('no' smallint ,")
                .append("('transaction_id' varchar(64) ,")
                .append("'name' varchar(200),")
                .append("'status' smallint ,")
                .append("'queue' varchar(50),")
                .append("'operation' varchar(50),")
                .append("'params' text)");
        template.execute(sqlBuilder.toString());
        sqlBuilder.delete(0, sqlBuilder.length());
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS ")
                .append(Recorder.TRANSACTION_FAIL_TABLE)
                .append("('id' varchar(64) Primary key,")
                .append("('transaction_id' varchar(64),")
                .append("'status' smallint ,")
                .append("'exception text,")
                .append("'params' text)");
        template.execute(sqlBuilder.toString());
    }

    public void transactionLog(Transaction transaction) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(Recorder.TRANSACTION_TABLE)
                .append("(id,name,status) values(?,?,?)");
        Object[] params = {transaction.getId(), transaction.getName(), transaction.getStatus()};
        template.update(sqlBuilder.toString(), params);
        List<TransactionOperation> operations = transaction.getOperations();
        for (TransactionOperation operation : operations) {
            sqlBuilder.delete(0, sqlBuilder.length());
            sqlBuilder.append("INSERT INTO ")
                    .append(Recorder.TRANSACTION_OPERATION_TABLE)
                    .append("(id,no,transaction_id,name,queue,status,operation,params)")
                    .append(" values(?,?,?,?,?,?,?,?)");
            Object[] operationParams = {operation.getId(), operation.getNo(), transaction.getId(), operation.getName(), operation.getQueue(),
                    operation.getStatus(), JSON.toJSONString(operation.getParams())};
            template.update(sqlBuilder.toString(), operationParams);
        }
    }


    public void updateOperationStatus(TransactionOperation operation) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(Recorder.TRANSACTION_OPERATION_TABLE).append(" SET status = ? WHERE id = ?");
        Object[] params = {operation.getStatus(), operation.getId()};
        template.update(sqlBuilder.toString(), params);
    }

    public void transactionFail(String transactionId, Exception e) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(Recorder.TRANSACTION_FAIL_TABLE)
                .append("(id,transaction_id,status,exception)")
                .append(" values(?,?,?,?)");
        Object[] params = {UUID.randomUUID().toString(), transactionId, Transaction.NON_EXECUTION, e.getMessage()};
        template.update(sqlBuilder.toString(), params);
    }


    public void updateTransactionStatus(Transaction transaction) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ");
        sqlBuilder.append(Recorder.TRANSACTION_TABLE).append(" SET status = ? WHERE id = ?");
        Object[] params = {transaction.getStatus(), transaction.getId()};
        template.update(sqlBuilder.toString(), params);
    }

    public List<Transaction> getUnfinishedTransaction() {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
        sqlBuilder.append(Recorder.TRANSACTION_TABLE).append("WHERE status = ? AND id NOT IN ( SELECT transaction_id FROM ").
                append(Recorder.TRANSACTION_FAIL_TABLE).append(")");
        List<Transaction> transactions = template.queryForList(sqlBuilder.toString(), new Object[]{Transaction.NON_EXECUTION}, Transaction.class);
        return putOperations(transactions);
    }

    public List<Transaction> getFailTransaction() {
        StringBuilder sqlBuilder = new StringBuilder("SELECT transaction_id FROM ");
        sqlBuilder.append(Recorder.TRANSACTION_FAIL_TABLE).append(" WHERE status = ?");
        List<String> ids = template.queryForList(sqlBuilder.toString(), new Object[]{Transaction.NON_EXECUTION}, String.class);
        sqlBuilder.delete(0, sqlBuilder.length());
        sqlBuilder.append("SELECT * FROM ").append(Recorder.TRANSACTION_TABLE).append(" WHERE id in (");
        for (String id : ids) {
            sqlBuilder.append(id).append(",");
        }
        sqlBuilder.append(")");
        List<Transaction> transactions = template.queryForList(sqlBuilder.toString(), Transaction.class);
        return putOperations(transactions);
    }


    List<Transaction> putOperations(List<Transaction> transactions) {
        if (transactions != null && !transactions.isEmpty()) {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM ");
            Map<String, Transaction> re = new HashMap();
            sqlBuilder.append(Recorder.TRANSACTION_OPERATION_TABLE).
                    append(" WHERE status = ").append(Transaction.NON_EXECUTION).append(" AND transaction_id in (");
            for (Transaction t : transactions) {
                re.put(t.getId(), t);
                sqlBuilder.append(t.getId()).append(",");
            }
            sqlBuilder.append(") ORDER BY transaction_id,no");
            List<TransactionOperation> operations = template.queryForList(sqlBuilder.toString(), TransactionOperation.class);
            Transaction t;
            String tid;
            List<TransactionOperation> transactionOperations;
            for (TransactionOperation o : operations) {
                tid = o.getTransactionId();
                t = re.get(tid);
                transactionOperations = t.getOperations();
                if (transactionOperations == null)
                    transactionOperations = new ArrayList();
                transactionOperations.add(o);
            }
            return new ArrayList<Transaction>(re.values());
        }
        return null;
    }
}
