package ec.edu.espe.master.config;

import ec.edu.espe.master.dto.RegisterRequestDto;
import ec.edu.espe.master.entity.Role;
import ec.edu.espe.master.repository.RoleRepository;
import ec.edu.espe.master.repository.UsersRepository;
import ec.edu.espe.master.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsersRepository usersRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Inicializando datos...");

        // 1. Crear Roles
        List<String> roles = Arrays.asList(
                "Administrador",
                "Encargado de Bodega",
                "Vendedor",
                "Asistente de Compras"
        );

        for (String roleName : roles) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .description("Rol de " + roleName)
                        .active(true)
                        .build();
                roleRepository.save(role);
                log.info("Rol '{}' creado.", roleName);
            }
        }

        // 2. Crear usuarios por defecto si no existen
        createUserIfNotExists("admin", "admin1234", "Administrador", "0000000001", "Admin", "I", "System");
        createUserIfNotExists("bodega", "bodega1234", "Encargado de Bodega", "0000000002", "Encargado", "B", "Bodega");
        createUserIfNotExists("vendedor", "vendedor1234", "Vendedor", "0000000003", "Vendedor", "V", "Ventas");
        createUserIfNotExists("asistente", "asistente1234", "Asistente de Compras", "0000000004", "Asistente", "A", "Compras");

        log.info("Inicialización de datos completada.");
    }

    private void createUserIfNotExists(String username, String password, String role, String dni, String firstName, String middleName, String lastName) {
        if (!usersRepository.existsByUsername(username)) {
            RegisterRequestDto request = RegisterRequestDto.builder()
                    .username(username)
                    .password(password)
                    .role(role)
                    .dni(dni)
                    .firstName(firstName)
                    .middleName(middleName)
                    .lastName(lastName)
                    .email(username + "@empresa.com")
                    .phoneNumber("099999999" + dni.substring(9))
                    .address("Direccion " + username)
                    .nationality("Ecuatoriana")
                    .build();
            authService.register(request);
            log.info("Usuario '{}' con rol '{}' creado.", username, role);
        }
    }
}
