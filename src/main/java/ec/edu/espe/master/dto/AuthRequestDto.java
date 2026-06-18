package ec.edu.espe.master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthRequestDto - Data Transfer Object para solicitud de login.
 * 
 * Utilizado en el endpoint POST /auth/login para recibir las credenciales
 * del usuario desde el cliente.
 * 
 * Anotaciones Lombok:
 * - @Data: Genera getters, setters, equals(), hashCode() y toString()
 * - @Builder: Permite construir el DTO con patrón Builder
 * - @AllArgsConstructor/@NoArgsConstructor: Constructores con/sin parámetros
 * 
 * Nota: Este DTO NO debe contener información sensible en respuestas.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {
    /**
     * Nombre de usuario para autenticación.
     * Debe coincidir con un usuario existente en BD.
     */
    private String username;
    
    /**
     * Contraseña en texto plano.
     * Se valida contra el hash almacenado en BD.
     * NUNCA se transmite en texto plano en producción (usar HTTPS).
     */
    private String password;
}
