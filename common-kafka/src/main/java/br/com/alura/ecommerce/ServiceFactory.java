package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
