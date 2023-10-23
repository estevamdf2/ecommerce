package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.consumer.ConsumerService;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
