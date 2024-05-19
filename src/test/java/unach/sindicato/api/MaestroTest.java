package unach.sindicato.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.service.escuela.MaestroService;
import unach.sindicato.api.util.UddRequester;
import unach.sindicato.api.utils.UddMapper;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.persistence.Token;
import unach.sindicato.util.JsonData;
import unach.sindicato.api.util.PersistenceTest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UddApiApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class MaestroTest implements PersistenceTest {
    @LocalServerPort int port;

    @Autowired MaestroService service;
    @Autowired TestRestTemplate restTemplate;
    @Autowired UddMapper mapper;

    UddRequester requester;

    @BeforeEach
    public void init() {
        requester = new UddRequester(restTemplate);
    }

    @Test
    public void testSave() {
        Credencial credencial = JsonData.CREDENTIALS.first(Credencial.class)
                .orElseThrow();

        var loginResponse = requester.login(
                "http://localhost:%s/udd/api/admin/auth/login".formatted(port),
                credencial
        );
        assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(loginResponse.getBody());

        String token = loginResponse
                .getBody()
                .jsonAs(Token.class)
                .getToken();

        var maestro = JsonData.MAESTROS.first()
                .orElseThrow();

        var saveResponse = requester.post(
                "http://localhost:%s/udd/api/maestros/auth/register".formatted(port),
                token,
                maestro
        );
        assertEquals(saveResponse.getStatusCode(), HttpStatus.CREATED);
        assertNotNull(saveResponse.getBody());

        ObjectId maestroId = saveResponse
                .getBody()
                .jsonAs(new TypeReference<Token<Maestro>>() {})
                .getDocument()
                .getId();

        boolean deletionResult = service.delete(maestroId);
        assertTrue(deletionResult);
    }
}
