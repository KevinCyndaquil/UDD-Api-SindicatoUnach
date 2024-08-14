package unach.sindicato.api.persistence.data;

import java.util.Arrays;

/**
 * @author Kevin Alejandro Francisco González
 * Contiene todos los roles que puede llegar a tener un Usuario de UDD.
 */

public enum RolesUsuario {
    administrador,
    maestro;

    public static RolesUsuario of(String role) {
        return Arrays.stream(RolesUsuario.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow();
    }
}
