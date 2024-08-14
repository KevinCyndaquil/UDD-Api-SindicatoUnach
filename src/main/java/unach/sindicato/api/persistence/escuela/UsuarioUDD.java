package unach.sindicato.api.persistence.escuela;

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
import unach.sindicato.api.persistence.data.Correo;
import unach.sindicato.api.persistence.data.RolesUsuario;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.groups.IdInfo;
import unach.sindicato.api.utils.persistence.Nombrable;
import unach.sindicato.api.utils.persistence.Unico;

import java.util.Collection;
import java.util.List;

/**
 * @author Kevin Alejandro Francisco González
 * Usuario generalizado abstracto para el proyecto UDD.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "rol")
@JsonSubTypes({
        @Type(value = Maestro.class, name = "maestro"),
        @Type(value = UddAdmin.class, name = "administrador")})

@Data
@EqualsAndHashCode(exclude = {"nombre", "apellido_paterno", "apellido_materno", "correo_institucional", "password", "salt"})
@Document(collection = "escuela")
public abstract class UsuarioUDD implements Unico, Nombrable, UserDetails {

    @Null(message = "No se debe proporcionar un id",
            groups = NotId.class)
    @NotNull(message = "Es necesario proporcionar una propiedad id",
            groups = IdInfo.class)
    private ObjectId id;
    @NotEmpty(message = "Se debe proporcionar un nombre",
            groups = InitInfo.class)
    @Pattern(message = "Nombre invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    private String nombre;
    @NotEmpty(message = "Se debe proporcionar un apellido paterno",
            groups = InitInfo.class)
    @Pattern(message = "Apellido invalido",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    private String apellido_paterno;
    @Pattern(message = "Se debe proporcionar un apellido materno",
            regexp = "(?U)^[\\p{Lu}\\p{M}\\d]+( [\\p{Lu}\\p{M}\\d]+)*$",
            groups = InitInfo.class)
    private String apellido_materno;
    @NotNull(message = "Se debe proporcionar un correo institucional",
            groups = InitInfo.class)
    @Valid
    private Correo correo_institucional;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotEmpty(message = "Se debe proporcionar una contraseña",
            groups = InitInfo.class)
    @Pattern(regexp = "^.{8,}$",
            message = "Las contraseñas deben contener al menos 8 caracteres",
            groups = InitInfo.class)
    private String password;
    @JsonIgnore
    private String salt;
    RolesUsuario rol;

    @Field("rol")
    @JsonGetter("rol")
    public abstract @NonNull RolesUsuario getRol();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRol().toString()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    @JsonIgnore
    public abstract String getUsername();
}
