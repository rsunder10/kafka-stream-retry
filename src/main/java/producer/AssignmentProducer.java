package producer;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import model.Assignment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Properties;

public class AssignmentProducer {

    public static void main(String[] args) {
        Properties properties = new Properties();

        // kafka bootstrap server
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(properties);
        long id = 1L;
        while (true) {
            System.out.println("Producing batch: " + id);
            try {
                producer.send(newRandomTransaction());
                Thread.sleep(1000);
                id += 1;
            } catch (InterruptedException e) {
                break;
            }
        }
        producer.close();
    }

    private static ProducerRecord<String, String> newRandomTransaction() {
        Gson gson = new Gson();
        Faker faker = new Faker();
        Timestamp now = Timestamp.from(Instant.now());

        Assignment assignment = Assignment.builder()
                .id((long) faker.number().numberBetween(1, 10))
                .createdAt(now.getTime())
                .build();

        // creates an empty json {}
        return new ProducerRecord<>("assignment-input-1",
                String.format("%s", assignment.getId()),
                gson.toJson(assignment));
    }
}

