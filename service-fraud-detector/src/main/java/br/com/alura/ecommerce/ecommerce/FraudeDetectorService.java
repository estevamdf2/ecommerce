package br.com.alura.ecommerce.ecommerce;

import br.com.alura.ecommerce.LocalDatabase;
import br.com.alura.ecommerce.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class FraudeDetectorService implements ConsumerService<Order> {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    public static void main(String[] args) {
      new ServiceRunner<>(FraudeDetectorService::new).start(1);
    }

    private final LocalDatabase database;

    FraudeDetectorService() throws SQLException {
        String sql = "create table Orders (" +
                "uuid varchar(200) primary key ,"+
                "is_fraud boolean)";
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExists(sql);
    }
    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }
    @Override
    public String getConsumerGroup() {
        return FraudeDetectorService.class.getSimpleName();
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("-------------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        var message = record.value();
        var order = message.getPayload();
        if(wasProcessed(order)){
            System.out.println("Order " + order.getOrderId() + " was already processed!!!");
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(isFraud(order)){
            // pretending that the fraud happens when the amount is >= 4500
            System.out.println("Order is a fraud!!! ");
            database.update("insert into Orders (uuid, is_fraud) values (?,true)", order.getOrderId());
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED",
                    order.getEmail(),
                    message.getId().continueWith(FraudeDetectorService.class.getSimpleName()),
                    order);
        } else{
            System.out.println("Approved: "+order);
            database.update("insert into Orders (uuid, is_fraud) values (?,false)", order.getOrderId());
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED",
                    order.getEmail(),
                    message.getId().continueWith(FraudeDetectorService.class.getSimpleName()),
                    order);
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    private static boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
