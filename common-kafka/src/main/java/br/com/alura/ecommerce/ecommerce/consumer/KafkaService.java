package br.com.alura.ecommerce.ecommerce.consumer;

import br.com.alura.ecommerce.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction<T> parse;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(parse, groupId, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(parse, groupId, properties);
        consumer.subscribe(topic);

    }

    private KafkaService(ConsumerFunction<T> parse, String groupId, Map<String, String> properties){
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, properties));
    }

    public void run() {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() +" registros");
                for (var record: records){
                    try {
                        parse.consume((ConsumerRecord<String, Message<T>>) record);
                    } catch (Exception e) {
                        // so far, just logging the exception for this message
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private Properties getProperties(String groupId, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1"); //Fazer o autocommit de 1 em 1.
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "largest");
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close()  {
        consumer.close();
    }
}
