package ec.edu.espe.master.controller;

import ec.edu.espe.master.dto.AuthRequestDto;
import ec.edu.espe.master.dto.AuthResponseDto;
import ec.edu.espe.master.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController - Controlador REST para operaciones de autenticación.
 * 
 * Maneja los endpoints públicos relacionados con autenticación:
 * - Login: Autentica un usuario y devuelve un token JWT
 * - Register: Registra un nuevo usuario en el sistema
 * 
 * Base URL: /auth
 * 
 * Anotaciones Spring:
 * - @RestController: Combina @Controller + @ResponseBody para retornar JSON
 * - @RequestMapping("/auth"): Mapea los endpoints bajo /auth
 * - @RequiredArgsConstructor: Inyecta dependencias automáticamente en constructor
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Inyección de AuthService para acceder a la lógica de autenticación.
     */
    private final AuthService authService;

    /**
     * Endpoint para login de usuario.
     * 
     * POST /auth/login
     * 
     * @param request Objeto con username y password
     * @return ResponseEntity con el token JWT en el body
     * @throws IllegalArgumentException Si las credenciales son inválidas
     * 
     * Ejemplo de request:
     * {
     *   "username": "juan.doe",
     *   "password": "miPassword123"
     * }
     * 
     * Ejemplo de response (200 OK):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
