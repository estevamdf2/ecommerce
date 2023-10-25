package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    CreateUserService() throws SQLException {
        String sql = "create table Users (" +
                "uuid varchar(200) primary key ,"+
                "email varchar(200))";
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists(sql);
    }

    public static void main(String[] args) throws SQLException {
        new ServiceRunner(CreateUserService::new).start(1);
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("-------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var order = record.value().getPayload();
        if(isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        } else{
        }

    }

    private void insertNewUser(String email) throws SQLException {
        String sql = "insert into Users (uuid, email) values (?,?)";
        var uuid = UUID.randomUUID().toString();

        database.update(sql, uuid, email);
    }

    private boolean isNewUser(String email) throws SQLException {
        var results = database.query("select uuid from Users where email = ? limit 1", email);
        return !results.next();

    }
}
