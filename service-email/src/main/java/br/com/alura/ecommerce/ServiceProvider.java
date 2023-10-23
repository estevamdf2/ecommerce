package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.KafkaService;

import java.util.Map;

public class ServiceProvider {
    public void run(ServiceFactory<Email> factory) {
        var emailService = factory.create();
        try(var service = new KafkaService<>(emailService.getConsumerGroup(),
                emailService.getTopic(),
                emailService::parse,
                Map.of())){
            service.run();
        }
    }
}
