package ru.practicum.producer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;

public class AvroSerializer implements Serializer<SpecificRecordBase> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();
    private BinaryEncoder encoder;

    @Override
    public byte[] serialize(String topic, SpecificRecordBase recordBase) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            if (recordBase != null) {
                DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(recordBase.getSchema());
                encoder = encoderFactory.binaryEncoder(stream, encoder);
                writer.write(recordBase, encoder);
                encoder.flush();
            }
            return stream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сериализовать объект Avro", e);
        }
    }
}