package ec.edu.espe.master.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter - Filtro de autenticación JWT para Spring Security.
 * 
 * Se ejecuta en CADA solicitud HTTP para:
 * 1. Extraer el token JWT del header Authorization
 * 2. Validar y decodificar el token
 * 3. Cargar los detalles del usuario
 * 4. Autenticar el usuario en Spring Security
 * 
 * Hereda de OncePerRequestFilter para garantizar ejecución única por solicitud.
 * 
 * Flujo de una solicitud:
 * Client -> HTTP Request -> Filter (extrae JWT) -> Valida -> Autentíca -> Endpoint
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Servicio JWT para extraer y validar tokens.
     */
    private final JwtService jwtService;
    
    /**
     * Servicio para cargar detalles del usuario desde BD.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Método principal que filtra cada solicitud HTTP.
     * 
     * @NonNull es obligatorio por OncePerRequestFilter
     * @param request Solicitud HTTP entrante
     * @param response Respuesta HTTP saliente
     * @param filterChain Cadena de filtros para pasar al siguiente filtro
     * 
     * Flujo:
     * 1. Extrae header Authorization
     * 2. Si no contiene "Bearer ", permite que siga (puede ser endpoint público)
     * 3. Extrae el JWT (desde posición 7 en adelante)
     * 4. Extrae el username del JWT
     * 5. Si el usuario no está autenticado en el contexto:
     *    - Carga los detalles del usuario
     *    - Valida que el token sea válido
     *    - Establece la autenticación en el contexto de Spring Security
     * 6. Pasa la solicitud al siguiente filtro en la cadena
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Extrae el header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Si no existe header o no empieza con "Bearer ", continua sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrae el token JWT (omite los 7 caracteres de "Bearer ")
        jwt = authHeader.substring(7);
        // Extrae el username del token
        username = jwtService.extractUsername(jwt);

        // Si el username existe y el usuario no está autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Carga los detalles del usuario desde BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Valida que el token no esté expirado y pertenezca al usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Crea un token de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // Agrega detalles adicionales (IP, tipo de sesion, etc)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Establece la autenticación en el contexto de seguridad de Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Pasa al siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}
