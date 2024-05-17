package unach.sindicato.api.utils;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import unach.sindicato.api.persistence.escuela.UddAdmin;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.persistence.Nombrable;
import unach.sindicato.api.utils.persistence.Unico;

import java.util.Collection;
import java.util.List;

/**
 * Usuario generalizado para el proyecto UDD.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "rol")
@JsonSubTypes({
        @Type(value = Maestro.class, name = "maestro"),
        @Type(value = UddAdmin.class, name = "administrador")})

@Data
@EqualsAndHashCode(exclude = {"nombre","apellido_paterno","apellido_materno","correo_institucional", "password", "salt"})
@Document(collection = "escuela")
public abstract class UddUser implements Unico, Nombrable, UserDetails {
    @Null(message = "No se debe proporcionar una propiedad id",
            groups = NotId.class)
    @NotNull(message = "Se requiere un identicador",
            groups = IdInfo.class)
    ObjectId id;
    @NotEmpty(message = "Se requiere un nombre",
            groups = InitInfo.class)
    @Pattern(message = "Nombre invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    String nombre;
    @NotEmpty(message = "Se requiere un apellido paterno", groups = InitInfo.class)
    @Pattern(message = "Apellido invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    String apellido_paterno;
    @Pattern(message = "Se requiere un apellido materno",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    String apellido_materno;
    @NotNull(message = "Se requiere un correo institucional",
            groups = InitInfo.class)
    @Valid Correo correo_institucional;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(message = "Se requiere una contraseña",
            groups = InitInfo.class)
    @Pattern(regexp = "^.{8,}$",
            message = "Las contraseñas deben tener al menos 8 caracteres",
            groups = InitInfo.class)
    String password;
    @JsonIgnore String salt;

    @Field("rol")@JsonGetter("rol")
    public abstract @NonNull Roles getRol();

    @Override@JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRol().toString()));
    }

    @Override@JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override@JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override@JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override@JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override@JsonIgnore
    public abstract String getUsername();
}
