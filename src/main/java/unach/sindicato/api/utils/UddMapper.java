package unach.sindicato.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * ObjectMapper generalizado para su uso en la API de UDD.
 */
public class UddMapper extends ObjectMapper {
    public UddMapper() {
        registerModule(new JavaTimeModule());
        registerModule(objectIdModule());

        setSerializationInclusion(JsonInclude.Include.NON_NULL);

        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * @return un modulo para un ObjectId.
     */
    private @NonNull Module objectIdModule() {
        SimpleModule module = new SimpleModule();

        module.addDeserializer(ObjectId.class, new JsonDeserializer<>() {
            @Override
            public ObjectId deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
                return new ObjectId(parser.getText());
            }
        });
        module.addSerializer(ObjectId.class, new JsonSerializer<>() {
            @Override
            public void serialize(ObjectId id, JsonGenerator generator, SerializerProvider prov) throws IOException {
                generator.writeString(id.toHexString());
            }
        });

        return module;
    }

    public static @NonNull String writeValue(Object value) {
        try {
            UddMapper mapper = new UddMapper();
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
