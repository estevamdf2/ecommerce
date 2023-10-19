package br.com.alura.ecommerce;

import java.util.UUID;

public class CorrelationId {

    private final String id;

    CorrelationId() {
        this.id = UUID.randomUUID().toString();
    }
}
