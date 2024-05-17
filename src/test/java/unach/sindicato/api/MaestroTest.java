package unach.sindicato.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.persistence.escuela.UddAdmin;
import unach.sindicato.api.repository.MaestroRepository;
import unach.sindicato.api.utils.UddMapper;
import unach.sindicato.api.utils.persistence.Credential;
import unach.sindicato.api.utils.persistence.Token;
import unach.sindicato.api.utils.response.UddResponse;
import unach.sindicato.util.JsonData;
import unach.sindicato.api.util.PersistenceTest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UddApiApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class MaestroTest implements PersistenceTest {

    @LocalServerPort int port;

    @Autowired TestRestTemplate restTemplate;
    @Autowired UddMapper mapper;

    @Autowired MaestroRepository repository;
    @Autowired MongoTransactionManager transactionManager;

    @BeforeAll
    static void init() {
        //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }

    @Test
    public void testSave() {
        Credential credential = JsonData.CREDENTIALS.first(Credential.class)
                .orElseThrow();

        var loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/udd/api/admin/auth/login",
                credential,
                UddResponse.Properties.class
        );
        assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(loginResponse.getBody());

        Maestro maestro = JsonData.MAESTROS.first(Maestro.class)
                .orElseThrow();

        TypeReference<Token<UddAdmin>> reference = new TypeReference<>() {};
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " +
                loginResponse.getBody().jsonAs(reference).getToken());
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<?> httpEntity = new HttpEntity<>(maestro, headers);

        var saveResponse = restTemplate.exchange(
                "http://localhost:" + port + "/udd/api/maestros/auth/register",
                HttpMethod.POST,
                httpEntity,
                UddResponse.Properties.class
        );
        assertEquals(saveResponse.getStatusCode(), HttpStatus.CREATED);
        assertNotNull(saveResponse.getBody());

        System.out.println(saveResponse.getBody().getJson());

        repository.deleteById(saveResponse.getBody().jsonAs(Maestro.class).getId());
    }
}
