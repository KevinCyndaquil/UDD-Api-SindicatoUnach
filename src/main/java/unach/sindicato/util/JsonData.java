package unach.sindicato.util;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.NonNull;
import unach.sindicato.api.utils.UddMapper;

import java.io.*;
import java.net.URL;
import java.util.*;

public enum JsonData {
    MAESTROS(Objects.requireNonNull(JsonData.class.getClassLoader().getResource(
            "collections/maestros.json"))),
    ADMINS(Objects.requireNonNull(JsonData.class.getClassLoader().getResource(
            "collections/admins.json"))),
    CREDENTIALS(Objects.requireNonNull(JsonData.class.getClassLoader().getResource(
            "collections/credentials.json"))),;

    final URL url;

    JsonData(URL url) {
        this.url = url;
    }

    public List<Map<String, Object>> content() {
        try {
            TypeReference<List<Map<String, Object>>> reference = new TypeReference<>() {};
            return new UddMapper().readValue(url, reference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> content(@NonNull Class<T> _class) {
        return content().stream()
                .map(m -> {
                    System.out.println(m);
                    return new UddMapper().convertValue(m, _class);
                })
                .toList();
    }

    public <T> @NonNull Optional<T> first(@NonNull Class<T> _class) {
        return content(_class).stream()
                .findFirst();
    }

    public <T> @NonNull Optional<T> get(@NonNull Class<T> _class, int index) {
        return Optional.of(content(_class).get(index));
    }

    public <T> Optional<T> whereFirst(@NonNull Class<T> _class, String param, Object value) {
        return content().stream()
                .filter(map -> map.entrySet()
                        .stream()
                        .anyMatch(e -> e.getKey().equals(param) && e.getValue().equals(value)))
                .findFirst()
                .map(f -> new UddMapper().convertValue(f, _class));
    }
}
