package br.com.alura.ecommerce.ecommerce.consumer;

import java.util.Map;
import java.util.concurrent.Callable;

public class ServiceProvider<T> implements Callable<Void> {
    private final ServiceFactory<T> factory;

    public ServiceProvider(ServiceFactory<T> factory){
        this.factory = factory;
    }
    public Void call() throws Exception{
        var emailService = factory.create();
        try(var service = new KafkaService<>(emailService.getConsumerGroup(),
                emailService.getTopic(),
                emailService::parse,
                Map.of())){
            service.run();
        }
        return null;
    }
}
