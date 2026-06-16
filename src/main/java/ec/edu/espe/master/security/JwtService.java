package ec.edu.espe.master.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService - Servicio para manejo de JSON Web Tokens (JWT).
 * 
 * Proporciona funcionalidades para:
 * - Generar tokens JWT con información de usuario y claims personalizados
 * - Validar y extraer información de tokens JWT
 * - Verificar la expiración de tokens
 * 
 * Utiliza la librería JJWT (Java JWT) para operaciones criptográficas.
 * El algoritmo utilizado es HMAC-SHA256 para firma.
 * 
 * Configuración:
 * - jwt.secret: Clave secreta en base64 (ambiente)
 * - jwt.expiration: Tiempo de expiración en milisegundos (default: 1 día)
 */
@Service
public class JwtService {

    /**
     * Clave secreta para firmar y validar tokens JWT.
     * Se lee de properties o usa un valor por defecto.
     * En producción, DEBE configurarse en variables de ambiente.
     */
    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    /**
     * Tiempo de expiración del token en milisegundos.
     * Por defecto: 86400000 ms = 1 día (24 horas)
     * Configurable en application.properties.
     */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Extrae el nombre de usuario del token JWT.
     * El username se almacena como "subject" en el token.
     * 
     * @param token Token JWT
     * @return Username/email contenido en el token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae un claim específico del token usando una función resolver.
     * Método genérico que permite obtener cualquier claim.
     * 
     * @param token Token JWT
     * @param claimsResolver Función que especifica qué claim extraer
     * @param <T> Tipo del valor del claim
     * @return Valor del claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Genera un token JWT sin claims adicionales.
     * 
     * @param userDetails Detalles del usuario autenticado
     * @return Token JWT firmado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token JWT con claims adicionales personalizados.
     * Los claims adicionales pueden incluir roles, permisos, etc.
     * 
     * @param extraClaims Mapa de claims personalizados
     * @param userDetails Detalles del usuario autenticado
     * @return Token JWT firmado con los claims incluidos
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si un token JWT es válido para un usuario específico.
     * Verifica:
     * 1. Que el username en el token coincida con el del usuario
     * 2. Que el token no esté expirado
     * 
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario para validar
     * @return true si es válido, false si no
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
