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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsersRepository usersRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList());

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        return AuthResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequestDto request) {
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + request.getRole()));

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
        
        person = personRepository.save(person);

        User user = User.builder()
                .person(person)
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        UserRole userRole = UserRole.builder()
                .id(new UserRoleId(person.getId(), role.getId()))
                .user(user)
                .role(role)
                .active(true)
                .build();
        
        user.getUserRoles().add(userRole);

        usersRepository.save(user);
    }
}
