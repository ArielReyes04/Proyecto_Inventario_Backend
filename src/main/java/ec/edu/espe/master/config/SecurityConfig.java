package ec.edu.espe.master.config;

import ec.edu.espe.master.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Configuración central de seguridad para Spring Security.
 * 
 * Anotaciones principales:
 * - @Configuration: Marca esta clase como fuente de configuración Spring
 * - @EnableWebSecurity: Habilita la protección de seguridad web
 * - @EnableMethodSecurity: Habilita seguridad a nivel de método (@PreAuthorize, @PostAuthorize)
 * 
 * Responsabilidades:
 * 1. Definir qué endpoints son públicos y cuáles requieren autenticación
 * 2. Configurar estrategia de sesión (stateless con JWT)
 * 3. Definir proveedor de autenticación
 * 4. Configurar CORS para permite requests desde frontend
 * 5. Deshabilitar CSRF (no necesario con JWT stateless)
 * 6. Registrar el filtro JWT
 * 7. Configurar codificación de contraseñas (BCrypt)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Filtro JWT personalizado que valida tokens en cada solicitud.
     */
    private final JwtAuthFilter jwtAuthFilter;
    
    /**
     * Servicio para cargar detalles de usuario (implementado por UserDetailsServiceImpl).
     */
    private final UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Define qué requests requieren autenticación y cuáles son públicos.
     * 
     * Configuraciones:
     * 1. CORS habilitado: Permite requests desde localhost:4200 (frontend Angular)
     * 2. CSRF deshabilitado: No necesario en APIs stateless con JWT
     * 3. Autorización:
     *    - /auth/login: Público (permite todos)
     *    - POST /api/users: Solo para rol "Administrador"
     *    - Resto: Requiere autenticación
     * 4. Sesión stateless: No almacena sesión, sólo JWT en cada request
     * 5. JwtAuthFilter: Se agrega ANTES del filtro de login estándar
     * 
     * @param http Objeto para configurar seguridad
     * @return SecurityFilterChain configurada
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Habilita CORS con configuración por defecto (usando corsConfigurationSource)
                .cors(org.springframework.security.config.Customizer.withDefaults())
                // Deshabilita CSRF (innecesario en APIs JWT stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Configura autorización de requests
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso público a login
                        .requestMatchers("/auth/login").permitAll()
                        // Solo administradores pueden crear usuarios
                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("Administrador")
                        // Todos los otros requests requieren autenticación
                        .anyRequest().authenticated()
                )
                // Configura manejo de sesión: STATELESS = sin sesión en servidor
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configura el proveedor de autenticación
                .authenticationProvider(authenticationProvider())
                // Registra el filtro JWT ANTES del filtro de login estándar
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura CORS (Cross-Origin Resource Sharing).
     * Permite que el frontend en localhost:4200 acceda a este backend.
     * 
     * Configuración:
     * - Origen: http://localhost:4200 (frontend Angular)
     * - Métodos: GET, POST, PUT, DELETE, PATCH, OPTIONS
     * - Headers: Authorization (para JWT), Content-Type
     * 
     * @return Fuente de configuración CORS
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        // Crea configuración CORS
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        // Define orígenes permitidos
        configuration.setAllowedOrigins(java.util.List.of("http://localhost:4200"));
        // Define métodos HTTP permitidos
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Define headers permitidos
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
        // Registra la configuración para todos los paths
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Proveedor de autenticación DAO (Data Access Object).
     * Utiliza UserDetailsService para cargar usuarios de BD.
     * Valida contraseñas usando PasswordEncoder (BCrypt).
     * 
     * @return AuthenticationProvider configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Crea el proveedor DAO con el UserDetailsService
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        // Configura el encoder de contraseñas
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Obtiene el gestor de autenticación de Spring Security.
     * Lo utiliza AuthServiceImpl para validar credenciales.
     * 
     * @param config Configuración de autenticación
     * @return AuthenticationManager
     * @throws Exception Si ocurre error al obtener el gestor
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura el encoder de contraseñas usando BCrypt.
     * BCrypt es un algoritmo de hashing adaptativo que es seguro contra ataques de fuerza bruta.
     * 
     * Características de BCrypt:
     * - Genera hashes de 60 caracteres
     * - Incorpora salt automáticamente
     * - Adaptativo: el factor de "strength" se puede aumentar
     * - Lento por diseño (protección contra ataques de fuerza bruta)
     * 
     * @return PasswordEncoder BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
