package unach.sindicato.api;

import lombok.NonNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import unach.sindicato.api.persistence.documentos.Pdf;
import unach.sindicato.api.persistence.escuela.Maestro;
import unach.sindicato.api.util.UddRequester;
import unach.sindicato.api.utils.Correo;
import unach.sindicato.api.utils.Formatos;
import unach.sindicato.api.utils.UddMapper;
import unach.sindicato.api.utils.persistence.Credential;
import unach.sindicato.api.utils.persistence.Token;
import unach.sindicato.util.JsonData;
import unach.sindicato.api.util.PersistenceTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
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
        Credential credential = JsonData.CREDENTIALS.first(Credential.class)
                .orElseThrow();

        var loginResponse = requester.login(
                "http://localhost:" + port + "/udd/api/admin/auth/login",
                credential
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
                "http://localhost:%s/udd/api/maestros/where/correo/is".formatted(port),
                token,
                correo
        );
        assertEquals(getByCorreoResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(getByCorreoResponse.getBody());

        Maestro erwin = getByCorreoResponse
                .getBody()
                .jsonAs(Maestro.class);

        Pdf documento = new Pdf();
        documento.setFormato(Formatos.ACTA_NACIMIENTO);
        documento.setBytes(generate(Objects.requireNonNull(DocumentoTest.class
                .getClassLoader()
                .getResource("pdf/reporte.pdf"))));
        erwin.getDocumentos().clear();
        erwin.getDocumentos().add(documento);

        var saveResponse = requester.post(
                "http://localhost:%s/udd/api/maestros/add/documentos".formatted(port),
                token,
                erwin
        );
        assertEquals(saveResponse.getStatusCode(), HttpStatus.OK);
        assertNotNull(saveResponse.getBody());

        System.out.println(saveResponse.getBody());
    }

    private byte @NonNull[] generate(@NonNull URL url) {
        File pdfFile;

        try {
            pdfFile = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try (PDDocument document = PDDocument.load(pdfFile);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();) {

            PDFRenderer renderer = new PDFRenderer(document);

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImageWithDPI(0, 300);
                ImageIO.write(image, "jpg", baos);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
