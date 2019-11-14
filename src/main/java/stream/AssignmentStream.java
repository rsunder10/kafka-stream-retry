package stream;

import com.google.gson.Gson;
import model.Assignment;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import java.util.Objects;
import java.util.Properties;

public class AssignmentStream {
    public static void main(String[] args) {
        Properties config = new Properties();
        Gson gson = new Gson();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "schedule-curing-alarms");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        StreamsBuilder builder = new StreamsBuilder();

        KeyValueBytesStoreSupplier failed = Stores.persistentKeyValueStore("failed");
        Materialized<String, String, KeyValueStore<Bytes, byte[]>> m = Materialized.as(failed);

        GlobalKTable<String, String> failedGlobalTable = builder.globalTable("assignment-failed-try-2", m.withKeySerde(Serdes.String()).withValueSerde(Serdes.String()));
        KStream<String, String> assignmentStream = builder.stream("assignment-input-1");
        KStream<String, Assignment>[] branch = assignmentStream.leftJoin(failedGlobalTable, (key, value) -> key, (assignment, failedAssignment) -> {
            Assignment receivedAssignment = gson.fromJson(assignment, Assignment.class);
            receivedAssignment.setIsAlreadyFailed(Objects.nonNull(failedAssignment));
            return receivedAssignment;
        })
                .peek((k, v) -> System.out.println(v))
                .branch((k, v) -> v.getIsAlreadyFailed(), (k, v) -> !v.getIsAlreadyFailed());
        branch[0].mapValues(Assignment::toString).to("assignment-failed-try-2");
        branch[1].mapValues(Assignment::toString).to("processing-assignment-1");

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
