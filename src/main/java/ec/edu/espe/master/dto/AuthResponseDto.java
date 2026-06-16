package ec.edu.espe.master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponseDto - Data Transfer Object para respuesta de autenticación.
 * 
 * Utilizado en el endpoint POST /auth/login para enviar el token JWT
 * generado al cliente después de la autenticación exitosa.
 * 
 * El cliente debe incluir este token en el encabezado Authorization
 * en formato: "Bearer <token>" para futuras solicitudes autenticadas.
 * 
 * Ejemplo de uso en cliente:
 * - Guardar el token en localStorage o sessionStorage
 * - Agregar a cada request: Authorization: Bearer <token>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    /**
     * Token JWT generado para la sesión del usuario.
     * Contiene:
     * - username (subject)
     * - roles (claims personalizados)
     * - fecha de emisión
     * - fecha de expiración
     * 
     * El token es firmado con clave privada y puede validarse sin acceso a BD.
     */
    private String token;
}
