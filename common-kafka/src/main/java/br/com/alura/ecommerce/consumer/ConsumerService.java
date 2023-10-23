package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

    public void parse(ConsumerRecord<String, Message<T>> record);
    String getTopic();
    String getConsumerGroup();
}
