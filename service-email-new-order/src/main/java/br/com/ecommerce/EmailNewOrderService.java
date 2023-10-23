package br.com.ecommerce;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.Email;
import br.com.alura.ecommerce.Message;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {

    private KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();
    private Message<Order> message;

    public static void main(String[] args) {
        var emailNewOrder = new EmailNewOrderService();

        try(var service = new KafkaService(EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                emailNewOrder::parse,
                Map.of())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-------------------------------------");
        System.out.println("Processing new email order");
        System.out.println(record.value());

        var emailCode = new Email("New order received", "Thank you for your order! We are processing your order!");
        var message = record.value();
        var order = message.getPayload();
        var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailCode);
    }
}