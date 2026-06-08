package ec.edu.espe.master.controller;

import ec.edu.espe.master.dto.RegisterRequestDto;
import ec.edu.espe.master.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ec.edu.espe.master.services.UserService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("Usuario creado exitosamente");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<ec.edu.espe.master.dto.response.UserResponse> updateUser(
            @PathVariable java.util.UUID id, 
            @RequestBody ec.edu.espe.master.dto.request.UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<String> deleteUser(@PathVariable java.util.UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAuthority('Administrador')")
    public ResponseEntity<ec.edu.espe.master.dto.response.UserResponse> toggleActive(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(userService.toggleActive(id));
    }
}
