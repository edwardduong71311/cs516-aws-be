package edward.duong.util;

import edward.duong.config.EnvConfig;
import edward.duong.payload.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class TokenHelper {
    private static final String SECRET_BASE64 = EnvConfig.TOKEN_SECRET;
    private static final byte[] DECODED_KEY = Base64.getDecoder().decode(SECRET_BASE64);
    private static final long EXPIRATION_TIME = EnvConfig.TOKEN_EXP;

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(DECODED_KEY))
                .compact();
    }

    public static String validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(DECODED_KEY)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String getEmailFromToken(Map<String, String> header) {
        String token = header.get("Authorization");
        if (Objects.isNull(token) || !token.startsWith("Bearer ")) {
            return null;
        }

        token = token.replace("Bearer ", "");
        String email = TokenHelper.validateToken(token);
        if (Objects.isNull(email)) {
            return null;
        }

        return email;
    }
}
