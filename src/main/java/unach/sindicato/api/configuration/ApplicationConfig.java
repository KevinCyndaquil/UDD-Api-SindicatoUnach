package unach.sindicato.api.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import unach.sindicato.api.service.auth.UddUserService;
import unach.sindicato.api.utils.UddMapper;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    final UddUserService authService;

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
}
