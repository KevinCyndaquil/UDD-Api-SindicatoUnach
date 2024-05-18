package unach.sindicato.api.configuration;

import com.mongodb.lang.Nullable;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import unach.sindicato.api.service.auth.JwtService;
import unach.sindicato.api.service.auth.UddUserService;
import unach.sindicato.api.utils.Roles;
import unach.sindicato.api.utils.UddLogger;
import unach.sindicato.api.utils.UddMapper;
import unach.sindicato.api.utils.UddUser;
import unach.sindicato.api.utils.errors.Errors;
import unach.sindicato.api.utils.persistence.Credential;
import unach.sindicato.api.utils.response.UddResponse;

import java.io.IOException;
import java.io.OutputStream;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    final JwtService jwtService;
    final UddUserService uddUserService;
    final UddMapper mapper;

    final UddLogger logger = new UddLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest httpRequest,
                                    @NonNull HttpServletResponse httpResonse,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filter(httpRequest);
            filterChain.doFilter(httpRequest, httpResonse);
        } catch (UnsupportedJwtException | MalformedJwtException |
                 SignatureException | IllegalArgumentException e) {
            logger.error(e, "JWT Filter Error");
            response(httpResonse, UddResponse.error()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("Token provided is unrecognized")
                    .error(Errors.INVALID_TOKEN_ERROR)
                    .build());
        } catch (ExpiredJwtException e) {
            logger.error(e, "JWT Filter Error");
            response(httpResonse, UddResponse.error()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("Token expired")
                    .error(Errors.INVALID_TOKEN_ERROR)
                    .build());
        } catch (UsernameNotFoundException e) {
            logger.error(e, "JWT Filter Error");
            response(httpResonse, UddResponse.error()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("User in token is unrecognized")
                    .error(Errors.INVALID_TOKEN_ERROR)
                    .build());
        }
    }

    private void filter(@NonNull HttpServletRequest httpRequest) {
        final String token = getToken(httpRequest);
        final ObjectId id;

        if (token == null) return;
        if (jwtService.isExpired(token)) return;
        id = new ObjectId(jwtService.parse(token).getSubject());

        if (SecurityContextHolder.getContext().getAuthentication() != null) return;

        Roles rol = Roles.of(jwtService.parse(token).get("rol", String.class));
        UddUser user = uddUserService.readById(id, rol);
        logger.info("User is enterring: " + user);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                Credential.by(user),
                user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private @Nullable String getToken(@NonNull HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void response(@NonNull HttpServletResponse httpResponse, @NonNull UddResponse message) {
        try {
            httpResponse.setStatus(message.getStatusCode().value());
            httpResponse.setContentType("application/json");

            try (OutputStream stream = httpResponse.getOutputStream()) {
                mapper.writeValue(stream, message.getBody());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
