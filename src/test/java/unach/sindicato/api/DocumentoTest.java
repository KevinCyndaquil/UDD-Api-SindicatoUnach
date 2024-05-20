package unach.sindicato.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unach.sindicato.api.persistence.documentos.Documento;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.util.UddRequester;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.Formatos;
import unach.sindicato.api.utils.UddMapper;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.persistence.Token;
import unach.sindicato.util.JsonData;
import unach.sindicato.api.util.PersistenceTest;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UddApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DocumentoTest implements PersistenceTest {

    @LocalServerPort int port;

    @Autowired TestRestTemplate restTemplate;
    @Autowired UddMapper mapper;

    UddRequester requester;

    @BeforeEach
    public void init() {
        this.requester = new UddRequester(restTemplate);
    }

    @Test
    @Override
    public void testSave() {
        Credencial credencial = JsonData.CREDENTIALS.first(Credencial.class)
                .orElseThrow();

        var loginResponse = requester.login(
                "http://localhost:" + port + "/udd/api/admin/auth/login",
                credencial
        );
        assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(loginResponse.getBody());

        String token = loginResponse
                .getBody()
                .jsonAs(Token.class)
                .getToken();

        Correo correo = new Correo();
        correo.setDireccion("erwin.bermudez@unach.mx");

        var getByCorreoResponse = requester.post(
                "http://localhost:%s/udd/api/maestros/where/correo-is".formatted(port),
                token,
                correo
        );
        assertEquals(getByCorreoResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(getByCorreoResponse.getBody());

        Maestro erwin = getByCorreoResponse
                .getBody()
                .jsonAs(Maestro.class);

        Pdf pdf = new Pdf();
        pdf.setFormato(Formatos.CAJA_AHORRO);
        try {
            Path path = Paths.get(Objects.requireNonNull(DocumentoTest.class
                            .getClassLoader()
                            .getResource("pdf/actividad3.pdf"))
                    .toURI()
                    .getPath());
            pdf.setBytes(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        erwin.getDocumentos().clear();
        erwin.getDocumentos().add(pdf);
        erwin.setEstatus(null);

        var saveResponse = requester.post(
                "http://localhost:%s/udd/api/maestros/add/documentos".formatted(port),
                token,
                erwin
        );
        assertEquals(saveResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(saveResponse.getBody());

        System.out.println(saveResponse.getBody());
    }

    @Test
    @Override
    public void testFindById() {

    }

    @Test
    @Override
    public void testFindAll() {

    }

    @Test
    @Override
    public void testUpdate() {

    }

    @Test
    public void testAddReporte() {
        Credencial credencial = JsonData.CREDENTIALS.first(Credencial.class)
                .orElseThrow();

        var loginResponse = requester.login(
                "http://localhost:" + port + "/udd/api/admin/auth/login",
                credencial
        );
        assertEquals(loginResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(loginResponse.getBody());

        String token = loginResponse
                .getBody()
                .jsonAs(Token.class)
                .getToken();

        Correo correo = new Correo();
        correo.setDireccion("erwin.bermudez@unach.mx");

        var getByCorreoResponse = requester.post(
                "http://localhost:%s/udd/api/maestros/where/correo-is".formatted(port),
                token,
                correo
        );
        assertEquals(getByCorreoResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(getByCorreoResponse.getBody());

        var erwin = getByCorreoResponse
                .getBody()
                .jsonAs(Maestro.class);

        Documento.Reporte reporte = Documento.Reporte.motivo(Documento.Estatus.REQUIERE_ACTUALIZAR);
        reporte.setDescripcion("wey noooooo");
        erwin.getDocumentos()
                .stream()
                .findFirst()
                .ifPresent(doc -> doc.setReporte(reporte));

        var saveResponse = requester.post(
                "http://localhost:%s/udd/api/admin/add/reportes".formatted(port),
                token,
                erwin
        );
        assertEquals(saveResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(saveResponse.getBody());

        System.out.println(saveResponse.getBody());
    }
}
