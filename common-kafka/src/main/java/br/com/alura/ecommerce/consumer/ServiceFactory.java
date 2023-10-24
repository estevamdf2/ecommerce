package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.consumer.ConsumerService;

import java.sql.SQLException;

public interface ServiceFactory<T> {
    ConsumerService<T> create() throws SQLException;
}
