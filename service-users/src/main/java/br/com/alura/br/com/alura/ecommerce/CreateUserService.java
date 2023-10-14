package br.com.alura.br.com.alura.ecommerce;

import br.com.alura.ecommerce.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        String sql = "create table Users (" +
                "uuid varchar(200) primary key ,"+
                "email varchar(200))";
        connection.createStatement().execute(sql);
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

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("-------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var order = record.value();
        if(isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        } else{
        }

    }

    private void insertNewUser(String email) throws SQLException {
        String sql = "insert into Users (uuid, email) values (?,?)";
        var insert = connection.prepareStatement(sql);
        insert.setString(1, "uuid");
        insert.setString(2, "email");
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
