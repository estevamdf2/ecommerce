package br.com.alura.br.com.alura.ecommerce;

import br.com.alura.ecommerce.KafkaService;
import br.com.alura.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        String sql = "create table Users (" +
                "uuid varchar(200) primary key ,"+
                "email varchar(200))";
        try{
            connection.createStatement().execute(sql);
        } catch (SQLException e){
            // be careful, the sql could be wrong, be reallly careful.
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws SQLException {
        var userService = new CreateUserService();

        try(var service = new KafkaService(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                userService::parse,
                Order.class,
                Map.of())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
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
        var insert = connection.prepareStatement(sql);
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("Usuário uuid e " +email + " adicionado");

    }

    private boolean isNewUser(String email) throws SQLException {
        String sql = "select uuid from Users where email = ? limit 1";
        var exists = connection.prepareStatement(sql);
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }

    private static boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
