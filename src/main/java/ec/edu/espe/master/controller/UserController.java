package ec.edu.espe.master.controller;

import ec.edu.espe.master.dto.request.UserRequest;
import ec.edu.espe.master.dto.response.UserResponse;
import ec.edu.espe.master.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    //@Autowired
    //private UserService userService;
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userRequest));
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserResponse> assigneRole(@PathVariable UUID userId, @PathVariable UUID roleId){
        return ResponseEntity.ok(userService.assigneRole(userId,roleId));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(java.security.Principal principal){
        return ResponseEntity.ok(userService.getCurrentUserProfile(principal.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody ec.edu.espe.master.dto.request.ProfileUpdateRequest request, java.security.Principal principal){
        return ResponseEntity.ok(userService.updateCurrentUserProfile(principal.getName(), request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ec.edu.espe.master.dto.request.PasswordChangeRequest request, java.security.Principal principal){
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }
}
