package br.com.alura.ecommerce.ecommerce.consumer;

import br.com.alura.ecommerce.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

    public void parse(ConsumerRecord<String, Message<T>> record) throws Exception;
    String getTopic();
    String getConsumerGroup();
}
