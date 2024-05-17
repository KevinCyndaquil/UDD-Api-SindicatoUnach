package unach.sindicato.api.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import unach.sindicato.api.utils.UddUser;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}") String SECRET;
    @Value("${jwt.expires-in}") long EXPIRES_IN;

    public String generate(@NonNull UddUser user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRES_IN);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("rol", user.getRol())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey())
                .compact();
    }

    public Claims parse(@NonNull String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isExpired(@NonNull String token) {
        Date expiryDate = parse(token).getExpiration();
        return expiryDate.before(new Date());
    }

    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
