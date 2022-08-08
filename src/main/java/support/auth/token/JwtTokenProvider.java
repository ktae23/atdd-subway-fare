package support.auth.token;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.List;

public class JwtTokenProvider {
    private String secretKey;
    private long validityInMilliseconds;

    public JwtTokenProvider(String secretKey, long validityInMilliseconds) {
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(String principal, List<String> roles, Integer age) {
        Claims claims = Jwts.claims().setSubject(principal);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim("roles", roles)
                .claim("age", age)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getPrincipal(String token) {
        return parseClaims(token).getBody().getSubject();
    }


    public List<String> getRoles(String token) {
        return (List<String>) getSpecificParameter(token, "roles", List.class);
    }

    public Integer getAge(String token) {
        return (Integer) getSpecificParameter(token, "age", Integer.class);
    }

    private Object getSpecificParameter(String token, String parameter, Class<?> clz) {
        return parseClaims(token).getBody().get(parameter, clz);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = parseClaims(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    }
}
