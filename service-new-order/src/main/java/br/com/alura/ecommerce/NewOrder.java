package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrder {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        sendMessage();
    }
    public static void sendMessage() throws ExecutionException, InterruptedException {
        var email = Math.random() + "@email.com";
        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            try(var emailDispatcher = new KafkaDispatcher<Email>()) {
                for (int i = 0; i < 10; i++) {
                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1).setScale(2, RoundingMode.HALF_UP);

                    var order = new Order(userId, amount, email);

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    var emailCode = new Email("New order received", "Thank you for your order! We are processing your order!");
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
                }
            }
        }

    }


}