package it.marmas.task.manager.api.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.marmas.task.manager.api.dto.PasswordResetTokenDto;
import it.marmas.task.manager.api.security.CustomUserDetails;

@Component
public class JwtUtil {
	  @Value("${jwt.secret}")
	  private String secretKey;
	  
	  private static final String PURPOSE="purpose";
	  private static final String PURPOSE_VALUE_RESET_PASSWORD="reset_password";
	  private static final String PURPOSE_VALUE_VALIDATE_ACCOUNT="validate-account";

	  
	    @Value("${jwt.access_token_validity}")
	    private String accessTokenExpiration;
	    
		@Value("${jwt.refresh_token_validity}")
 	  	private String refreshTokenExpiration;
	    

    private Key getKey() {
    	return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    
    // Estrae username dal token
    public    String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Estrae una qualunque informazione dal token
    public   <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private   Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // Verifica se il token è scaduto
    private   Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Estrae la data di scadenza
    public    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Metodo di validazione del token
    public   Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // (Opzionale) metodo per generare token, se serve
    public String generateAccessToken(CustomUserDetails userDetails) {
    	return generateGenericToken(userDetails,accessTokenExpiration);
     
    }
    public String generateRefreshToken(CustomUserDetails userDetails) {
    	return generateGenericToken(userDetails, refreshTokenExpiration);
    }
    private String generateGenericToken(CustomUserDetails userDetails,String duration) {
    		long durationL=Long.parseLong(duration) ;
    	   return Jwts.builder()
    	            .setSubject(userDetails.getUsername())
    	            .claim("roles", userDetails.getAuthorities())
    	            .setIssuedAt(new Date())
    	            .setExpiration(new Date(System.currentTimeMillis() + durationL))
    	            .signWith(getKey())
    	            .compact();
    }
    
    
    public PasswordResetTokenDto generateTokenForResetPassword(String email) {
     	long expirationTime= 1000*60*15; //15 minutes
    	Date expiry= new Date( System.currentTimeMillis()+ expirationTime);
    String token=	Jwts.builder()
    	.setSubject(email)
    	.claim(PURPOSE,PURPOSE_VALUE_RESET_PASSWORD)
    	.setIssuedAt(new Date())
    	.setExpiration(expiry)
    	.signWith(getKey())
    	.compact();
    PasswordResetTokenDto dto = new PasswordResetTokenDto();
    dto.setToken(token);
    LocalDateTime expiryLdt=Instant.ofEpochMilli(
    		expiry.getTime())
    		.atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    
    dto.setExpiryDate(expiryLdt);
    return dto;
    		
    
    }

    
    public String generateTokenForValidateAccount(String email) {
    	Date expiry= new Date(System.currentTimeMillis()+ (60* 1000* 15));
    	return Jwts.builder()
    			.setSubject(email)
    			.claim(PURPOSE, PURPOSE_VALUE_VALIDATE_ACCOUNT)
    			.setIssuedAt(new Date())
    			.setExpiration(expiry)
    			.signWith(getKey())
    			.compact();
    			
    }
}
