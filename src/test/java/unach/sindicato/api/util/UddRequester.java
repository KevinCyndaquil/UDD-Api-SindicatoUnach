package unach.sindicato.api.util;

import lombok.NonNull;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import unach.sindicato.api.utils.persistence.Credencial;
import unach.sindicato.api.utils.response.UddResponse;

public class UddRequester {
    final TestRestTemplate restTemplate;

    public UddRequester(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<UddResponse.Properties> login(String url, Credencial credencial) {
        return restTemplate.postForEntity(
                url,
                credencial,
                UddResponse.Properties.class
        );
    }

    public ResponseEntity<UddResponse.Properties> post(
            @NonNull String url,
            @NonNull String token,
            Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                UddResponse.Properties.class
        );
    }

    public ResponseEntity<UddResponse.Properties> get(
            @NonNull String url,
            @NonNull String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UddResponse.Properties.class
        );
    }
}
