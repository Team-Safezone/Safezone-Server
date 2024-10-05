package KickIt.server;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    // access token
    public String createAccessToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofDays(10).toMillis());
        return Jwts.builder()
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }



    /* 혹시 몰라서 만들어 둠
    // refresh token
    public String createRefreshJwt(long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofDays(30).toMillis());
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, Secrets.JWT_REFRESH_SECRET_KEY)
                .compact();
    }
    */


}
