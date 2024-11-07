package KickIt.server.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    // 토큰으로 사용자 이메일 추출
    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            return claims.get("email", String.class);  // 이메일 반환
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("JWT token is expired");
        } catch (Exception e) {
            throw new IllegalArgumentException("JWT token is invalid");
        }
    }

    // 토큰에서 만료일자 추출
    private Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

    // 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 토큰이 유효한지 확인 (사용자 이메일과 토큰의 만료 여부 확인)
    public boolean validateToken(String token, String email) {
        final String extractedEmail = getEmailFromToken(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

}
