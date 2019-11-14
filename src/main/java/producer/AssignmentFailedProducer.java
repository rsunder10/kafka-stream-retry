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
import java.util.Scanner;

public class AssignmentFailedProducer {

    public static void main(String[] args) {
        Properties properties = new Properties();

        // kafka bootstrap server
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<>(properties);
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter failed assignment");
                producer.send(new ProducerRecord<>("assignment-failed-try-2", scanner.nextLine(), scanner.nextLine()));
            } catch (Exception e) {
                break;
            }
        }
        producer.close();
    }

}

