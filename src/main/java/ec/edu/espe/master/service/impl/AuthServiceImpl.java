package ec.edu.espe.master.service.impl;

import ec.edu.espe.master.dto.AuthRequestDto;
import ec.edu.espe.master.dto.AuthResponseDto;
import ec.edu.espe.master.dto.RegisterRequestDto;
import ec.edu.espe.master.entity.Person;
import ec.edu.espe.master.entity.Role;
import ec.edu.espe.master.entity.User;
import ec.edu.espe.master.entity.UserRole;
import ec.edu.espe.master.entity.UserRoleId;
import ec.edu.espe.master.repository.PersonRepository;
import ec.edu.espe.master.repository.RoleRepository;
import ec.edu.espe.master.repository.UsersRepository;
import ec.edu.espe.master.security.JwtService;
import ec.edu.espe.master.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthServiceImpl - Implementación del servicio de autenticación.
 * 
 * Maneja la lógica de negocio para:
 * - Login: Autentica credenciales y genera JWT
 * - Registro: Crea nuevos usuarios con sus datos personales y roles
 * 
 * Utiliza inyección de dependencias para acceder a:
 * - Repositorios: Acceso a datos de usuarios, personas y roles
 * - JwtService: Generación de tokens JWT
 * - AuthenticationManager: Validación de credenciales Spring Security
 * - PasswordEncoder: Hashing seguro de contraseñas
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * Repositorio para operaciones CRUD en la tabla users.
     */
    private final UsersRepository usersRepository;
    
    /**
     * Repositorio para operaciones CRUD en la tabla persons.
     */
    private final PersonRepository personRepository;
    
    /**
     * Repositorio para operaciones CRUD en la tabla roles.
     */
    private final RoleRepository roleRepository;
    
    /**
     * Codificador de contraseñas usando BCrypt.
     * Proporciona métodos para hashear y validar contraseñas.
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Servicio para manejo de JWT.
     * Genera y valida tokens de autenticación.
     */
    private final JwtService jwtService;
    
    /**
     * Gestor de autenticación de Spring Security.
     * Valida las credenciales del usuario.
     */
    private final AuthenticationManager authenticationManager;
    
    /**
     * Servicio para cargar detalles del usuario.
     * Implementado por UserDetailsServiceImpl.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * Flujo:
     * 1. Valida las credenciales contra Spring Security
     * 2. Carga los detalles del usuario (username, password, authorities)
     * 3. Extrae los roles del usuario
     * 4. Genera un JWT con los roles como claims adicionales
     * 5. Retorna el token en la respuesta
     * 
     * @param request Contiene username y password
     * @return AuthResponseDto con el token JWT
     * @throws AuthenticationException Si las credenciales son inválidas
     */
    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        // Valida username y password contra la BD
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // Carga los detalles del usuario autenticado
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        
        // Prepara los claims adicionales (roles del usuario)
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList());

        // Genera el token JWT
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Flujo transaccional (ACID):
     * 1. Valida que el username no exista
     * 2. Obtiene el rol especificado en la solicitud
     * 3. Crea y persiste la entidad Person con datos personales
     * 4. Crea la entidad User con contraseña hasheada
     * 5. Asocia el rol al usuario mediante UserRole
     * 6. Persiste todo en BD
     * 
     * Si ocurre cualquier error, toda la transacción se revierte.
     * 
     * @param request Contiene datos personales, username, password y rol
     * @throws IllegalArgumentException Si username existe o role no existe
     */
    @Override
    @Transactional
    public void register(RegisterRequestDto request) {
        // Valida que el username no exista
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Obtiene el rol del repositorio
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + request.getRole()));

        // Crea la entidad Person con los datos personales
        Person person = Person.builder()
                .dni(request.getDni())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .nationality(request.getNationality())
                .active(true)
                .build();
        
        // Persiste la Person en BD
        person = personRepository.save(person);

        // Crea la entidad User con contraseña hasheada
        User user = User.builder()
                .person(person)
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        // Asocia el rol al usuario a través de UserRole
        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(person.getId(), role.getId()))
                .user(user)
                .role(role)
                .active(true)
                .build();
        
        // Agrega el rol al conjunto de roles del usuario
        user.getUserRoles().add(userRole);

        // Persiste el User (y sus UserRoles en cascada) en BD
        usersRepository.save(user);
    }
}
