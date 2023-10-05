package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class NewOrder {

    public void sendMessage() throws ExecutionException, InterruptedException {
        var producer = new KafkaProducer<String, String>(properties());
        var value = "1312, 1001, 12000";
        var record = new ProducerRecord<>("ECOMMERCE_NEW_ORDER",value,value);
        var res = producer.send(record, (data, ex) ->{
            if(ex != null){
                ex.printStackTrace();
                return;
            }
            System.out.println("sucesso enviando... "+data.topic() + ":::partition "+data.partition()+ "/ offset "  + data.offset() + "/ timestamp " + data.timestamp());
        }).get();
    }

    private Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }
}