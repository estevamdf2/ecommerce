package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        sendMessage();
    }

    public static void sendMessage() throws ExecutionException, InterruptedException {

        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            for (int i = 0; i < 10; i++) {
                var userId = UUID.randomUUID().toString();
                var orderId = UUID.randomUUID().toString();
                var amount = new BigDecimal(Math.random() * 5000 + 1).setScale(2, RoundingMode.HALF_UP);
                var email = "email@email.com";
                var order = new Order(userId, amount, email);
                var id = new CorrelationId(NewOrderMain.class.getSimpleName());

                orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, id, order);
            }
        }

    }


}