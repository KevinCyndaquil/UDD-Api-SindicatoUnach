package unach.sindicato.api.utils;

import java.util.Arrays;

public enum Roles {
    administrador,
    maestro;

    public static Roles of(String role) {
        return Arrays.stream(Roles.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow();
    }
}
