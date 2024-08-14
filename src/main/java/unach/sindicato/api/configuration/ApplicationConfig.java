package unach.sindicato.api.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.web.client.RestTemplate;
import unach.sindicato.api.service.auth.UsuarioUDDService;
import unach.sindicato.api.utils.UddMapper;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    final UsuarioUDDService authService;

    @Bean
    public UddMapper uddMapper() {
        return new UddMapper();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        return provider;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }
}
