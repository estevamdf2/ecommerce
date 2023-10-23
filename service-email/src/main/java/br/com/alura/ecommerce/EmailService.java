package br.com.alura.ecommerce;
import br.com.alura.ecommerce.consumer.ConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService implements ConsumerService<Email> {
    public static void main(String[] args) {
        new ServiceProvider().run(EmailService::new);
    }
    @Override
    public void parse(ConsumerRecord<String, Message<Email>> record) {
        System.out.println("-------------------------------------");
        System.out.println("Send email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.partition());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("email send");
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }
}