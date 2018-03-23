import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;

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
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(TRANSACTION_TABLE)
                .append("('id' varchar(64) Primary key,")
                .append("'name' varchar(200),")
                .append("'status' smallint ,")
                .append("'createTime' datetime DEFAULT CURRENT_TIMESTAMP)");
        template.execute(builder.toString());
        builder.delete(0, builder.length());
        builder.append("CREATE TABLE IF NOT EXISTS ")
                .append(TRANSACTION_OPERATION_TABLE)
                .append("('id' varchar(64) Primary key,")
                .append("('transaction_id' varchar(64) ,")
                .append("'name' varchar(200),")
                .append("'status' smallint ,")
                .append("'queue' varchar(50),")
                .append("'operation' varchar(50),")
                .append("'param' text)");
        template.execute(builder.toString());
        builder.delete(0, builder.length());
        builder.append("CREATE TABLE IF NOT EXISTS ")
                .append(TRANSACTION_FAIL_TABLE)
                .append("('id' varchar(64) Primary key,")
                .append("('transaction_id' varchar(64),")
                .append("'operation_name' varchar(200),")
                .append("'status' smallint NOT NULL,")
                .append("'operation_queue' varchar(50),")
                .append("'exception" +
                        "' text,")
                .append("'param' text)");
    }

    public void transactionLog(Transaction transaction) {

    }

    public void operationLog(TransactionMessage message) {

    }

    public void updateOperationStatus(TransactionMessage message) {

    }

    public void updateTransactionStatus(TransactionMessage message) {

    }

    public List<Transaction> getUnfinishedTransaction() {
        return null;
    }
}
