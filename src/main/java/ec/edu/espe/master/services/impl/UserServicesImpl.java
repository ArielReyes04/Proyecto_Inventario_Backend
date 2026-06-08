package ec.edu.espe.master.services.impl;

import ec.edu.espe.master.dto.request.UserRequest;
import ec.edu.espe.master.dto.response.PersonResponse;
import ec.edu.espe.master.dto.response.UserResponse;
import ec.edu.espe.master.entity.*;
import ec.edu.espe.master.repository.PersonRepository;
import ec.edu.espe.master.repository.RoleRepository;
import ec.edu.espe.master.repository.UserRoleRepository;
import ec.edu.espe.master.repository.UsersRepository;
import ec.edu.espe.master.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServicesImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return usersRepository.findAll().stream()
                .filter(u -> u.getPerson() != null && (u.getPerson().getDeleted() == null || !u.getPerson().getDeleted()))
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (personRepository.existsByDni(userRequest.getDni()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dni already exists");

        if (personRepository.existsByEmail(userRequest.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");

        Person objPerson = Person.builder()
                .dni(userRequest.getDni())
                .firstName(userRequest.getFirstName())
                .middleName(userRequest.getMiddleName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber()) // ya es String
                .nationality(userRequest.getNationality())
                .address(userRequest.getAddress())
                .build();

        objPerson = personRepository.save(objPerson);

        // Generar username con la función personalizada
        String username = generateUsername(objPerson);

        // Hashear el DNI como contraseña inicial
        String hashedPassword = hashPassword(objPerson.getDni());

        User objUser = User.builder()
                .person(objPerson)
                .username(username)
                .passwordHash(hashedPassword)
                .build();

        objUser = usersRepository.save(objUser);

        return mapToUserResponse(objUser);
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));
        
        Person person = user.getPerson();
        // Prevent duplicate DNI or Email if changed
        if (!person.getDni().equals(userRequest.getDni()) && personRepository.existsByDni(userRequest.getDni()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI ya está en uso");
        if (!person.getEmail().equals(userRequest.getEmail()) && personRepository.existsByEmail(userRequest.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El Email ya está en uso");

        person.setDni(userRequest.getDni());
        person.setFirstName(userRequest.getFirstName());
        person.setMiddleName(userRequest.getMiddleName());
        person.setLastName(userRequest.getLastName());
        person.setEmail(userRequest.getEmail());
        person.setPhoneNumber(userRequest.getPhoneNumber());
        person.setNationality(userRequest.getNationality());
        person.setAddress(userRequest.getAddress());
        
        personRepository.save(person);
        return mapToUserResponse(user);
    }

    @Override
    public void deleteUser(UUID userID) {
        User user = usersRepository.findById(userID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));
        Person person = user.getPerson();
        person.setDeleted(true);
        personRepository.save(person);
    }

    @Override
    public UserResponse toggleActive(UUID userID) {
        User user = usersRepository.findById(userID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));
        Person person = user.getPerson();
        person.setActive(person.getActive() == null ? false : !person.getActive());
        personRepository.save(person);
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse assigneRole(UUID userId, UUID roleId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no existe"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Role no existe"));

        if (userRoleRepository.existsByRoleAndUser(role, user))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El usuario ya tiene asignado un rol");

        UserRoleId urId = new UserRoleId(userId, roleId);

        UserRole userRole = UserRole.builder()
                .id(urId)
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);
        return mapToUserResponse( usersRepository.findById(userId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no existe")));

    }

    @Override
    public UserResponse removeRole(UUID userId, UUID roleId) {
        return null;
    }

    @Override
    public UserResponse getCurrentUserProfile(String username) {
        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateCurrentUserProfile(String username, ec.edu.espe.master.dto.request.ProfileUpdateRequest request) {
        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        Person person = user.getPerson();
        if (!person.getEmail().equals(request.getEmail()) && personRepository.existsByEmail(request.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El Email ya está en uso");

        person.setFirstName(request.getFirstName());
        person.setMiddleName(request.getMiddleName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setPhoneNumber(request.getPhoneNumber());
        person.setNationality(request.getNationality());
        person.setAddress(request.getAddress());
        
        personRepository.save(person);
        return mapToUserResponse(user);
    }

    @Override
    public void changePassword(String username, ec.edu.espe.master.dto.request.PasswordChangeRequest request) {
        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual no es correcta");
        }
        
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);
    }

    // Función personalizada para generar username según ejemplos:
    // anthony nestor villarreal macias -> anvillarrealm
    // andrea nicole villarreal moran -> anvillarrealm1
    private String generateUsername(Person person) {
        String firstInitial = "";
        String middleInitial = "";
        String paternalSurname = "";

        if (person.getFirstName() != null && !person.getFirstName().isEmpty()) {
            firstInitial = person.getFirstName().substring(0, 1).toLowerCase();
        }
        if (person.getMiddleName() != null && !person.getMiddleName().isEmpty()) {
            middleInitial = person.getMiddleName().substring(0, 1).toLowerCase();
        }
        // Tomamos el primer apellido (asumimos que es la primera palabra antes de un espacio)
        String fullLastName = person.getLastName();
        if (fullLastName != null && !fullLastName.isEmpty()) {
            String[] parts = fullLastName.trim().split("\\s+");
            if (parts.length > 0) {
                paternalSurname = parts[0].toLowerCase();
            }
        }

        String baseUsername = (firstInitial + middleInitial + paternalSurname)
                .replaceAll("[^a-z0-9]", ""); // eliminar caracteres no alfanuméricos

        // Verificar existencia y generar sufijo numérico si es necesario
        String candidate = baseUsername;
        int suffix = 1;
        while (usersRepository.existsByUsername(candidate)) {
            candidate = baseUsername + suffix;
            suffix++;
        }

        return candidate;
    }

    // Función personalizada para hashear el DNI (MD5 de 32 caracteres hexadecimales)
    private String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(plainPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString(); // 32 caracteres
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        List<String> roles = user.getUserRoles().stream()
                .filter(UserRole::getActive)
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        Person person = user.getPerson();
        PersonResponse personResponse = PersonResponse.builder()
                .id(person.getId())
                .dni(person.getDni())
                .firstName(person.getFirstName())
                .middleName(person.getMiddleName())
                .lastName(person.getLastName())
                .email(person.getEmail())
                .phoneNumber(person.getPhoneNumber())
                .address(person.getAddress())
                .nationality(person.getNationality())
                .active(person.getActive())
                .build();

        return UserResponse.builder()
                .id(user.getId().toString()) // Corregido: UUID a String
                .username(user.getUsername())
                .active(user.getPerson().getActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .person(personResponse)
                .roles(roles)
                .build();
    }
}