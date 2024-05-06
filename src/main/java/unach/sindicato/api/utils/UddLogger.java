package unach.sindicato.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UddLogger {
    final Logger logger;

    public UddLogger(Class<?> _class) {
        this.logger = LoggerFactory.getLogger(_class);
    }

    protected ZoneId getZoneId() {
        return ZoneId.of("America/Mexico_City");
    }

    protected String now() {
        return LocalDateTime.now(getZoneId())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public void info(String message) {
        logger.info("%s: %s"
                .formatted(now(), message));
    }

    public void info(String message, Class<?> at) {
        logger.info("%s: %s at %s"
                .formatted(now(), message, at));
    }

    public void error(Exception ex, String message) {
        logger.error("%s: %s".formatted(now(), message), ex);
    }

    public void error(Exception ex, String message, Class<?> at) {
        logger.error("%s: %s at %s".formatted(now(), message, at), ex);
    }

    public void post(Class<?> at) {
        info("Petición POST", at);
    }

    public void get(Class<?> at) {
        info("Petición GET", at);
    }

    public void put(Class<?> at) {
        info("Petición PUT", at);
    }

    public void delete(Class<?> at) {
        info("Petición DELETE", at);
    }
}
