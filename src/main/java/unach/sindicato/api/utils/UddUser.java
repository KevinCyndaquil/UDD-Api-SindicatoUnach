package unach.sindicato.api.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
import unach.sindicato.api.persistence.sujetos.Administrador;
import unach.sindicato.api.persistence.sujetos.Maestro;
import unach.sindicato.api.utils.groups.PostInfo;
import unach.sindicato.api.utils.groups.PutInfo;
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
        @Type(value = Administrador.class, name = "administrador")})

@Data
@EqualsAndHashCode(exclude = {"nombre","apellido_paterno","apellido_materno","correo"})
@Document(collection = "sujetos")
public abstract class UddUser implements Unico, Nombrable, UserDetails {
    @Null(groups = PostInfo.class)
    @NotNull(groups = PutInfo.class)
    ObjectId id;
    @NotEmpty(groups = PostInfo.class)
    @Pattern(regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$", groups = PostInfo.class)
    String nombre;
    @NotEmpty(groups = PostInfo.class)
    @Pattern(regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+$", groups = PostInfo.class)
    String apellido_paterno;
    @Pattern(regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+$", groups = PostInfo.class)
    String apellido_materno;
    @Valid
    @NotNull(groups = PostInfo.class)
    Correo correo;
    @JsonProperty(access = Access.WRITE_ONLY)
    @NotEmpty(groups = PostInfo.class)
    @Pattern(regexp = "^.{8,}$",
            message = "Las contraseñas deben tener al menos 8 caracteres",
            groups = PostInfo.class)
    String password;
    @JsonIgnore
    @Null(groups = PostInfo.class)
    String salt;

    @Field("rol")
    public abstract @NonNull Roles getRol();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRol().toString()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
