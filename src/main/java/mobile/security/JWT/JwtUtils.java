package mobile.security.JWT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import mobile.security.DTO.AppUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${apps.security.secret}")
    private String jwtSecret;

    @Value("${apps.security.header-string:Authorization}")
    private String headerString;

    @Value("${apps.security.token-prefix:Bearer }")
    private String tokenPrefix;

    @Value("${apps.security.jwtExpirationMs:86400000}")
    private int jwtExpirationMs;

    @Value("${apps.security.refreshJwtExpirationMs:604800000}")
    private int refreshJwtExpirationMs;

    public String generateJwtToken(AppUserDetail userPrincipal) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .withClaim("roleNames", userPrincipal.getRoles().stream().collect(Collectors.toList()))
                .withClaim("rolePermissions", userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public String generateRefreshJwtToken(AppUserDetail userPrincipal) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshJwtExpirationMs))
                .sign(algorithm);
    }

    public String getUserNameFromJwtToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

    public Collection<SimpleGrantedAuthority> getAuthoritiesFromJwtToken(String token) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        try {
            DecodedJWT jwt = JWT.decode(token);
            List<String> permissions = jwt.getClaim("rolePermissions").asList(String.class);
            if (permissions != null) {
                for (String perm : permissions) {
                    authorities.add(new SimpleGrantedAuthority(perm));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse authorities from JWT: {}", e.getMessage());
        }
        return authorities;
    }

    public String generateEmailJwtToken(String username) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .sign(algorithm);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            String token = authToken.replace(tokenPrefix, "").trim();
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public boolean validateExpiredToken(String authToken) {
        try {
            DecodedJWT jwt = JWT.decode(authToken);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
