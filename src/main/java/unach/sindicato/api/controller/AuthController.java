package unach.sindicato.api.controller;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import unach.sindicato.api.controller.persistence.PersistenceController;
import unach.sindicato.api.service.auth.AuthService;
import unach.sindicato.api.utils.UddLogger;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.groups.InitInfo;
import unach.sindicato.api.utils.groups.NotId;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.response.UddResponse;

public interface AuthController <U extends UddUser> {
    UddLogger logger = new UddLogger(PersistenceController.class);

    @NonNull AuthService<U> service();

    @PostMapping("/auth/register")
    default UddResponse register(@RequestBody@Validated({InitInfo.class, NotId.class}) U u) {
        logger.post(getClass());

        return UddResponse.collection()
                .status(HttpStatus.CREATED)
                .message("%s %s registrado correctamente"
                        .formatted(service().clazz().getSimpleName(), u.getNombre()))
                .collection(service().register(u))
                .build();
    }

    @PostMapping("/auth/login")
    default UddResponse login(@RequestBody@Validated({InitInfo.class}) Credencial credencial) {
        logger.post(getClass());

        var token = service().login(credencial);

        return UddResponse.collection()
                .status(HttpStatus.OK)
                .message("%s %s logeado correctamente"
                        .formatted(service().clazz().getSimpleName(), token.getDocument().getNombre()))
                .collection(token)
                .build();
    }
}
