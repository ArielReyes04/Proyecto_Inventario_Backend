package ec.edu.espe.master.service.impl;

import ec.edu.espe.master.dto.AuthRequestDto;
import ec.edu.espe.master.dto.AuthResponseDto;
import ec.edu.espe.master.dto.RegisterRequestDto;
import ec.edu.espe.master.entity.Person;
import ec.edu.espe.master.entity.Role;
import ec.edu.espe.master.entity.User;
import ec.edu.espe.master.repository.PersonRepository;
import ec.edu.espe.master.repository.RoleRepository;
import ec.edu.espe.master.repository.UsersRepository;
import ec.edu.espe.master.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLoginSuccess() {
        AuthRequestDto request = new AuthRequestDto();
        request.setUsername("admin");
        request.setPassword("admin123");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(any(), eq(userDetails))).thenReturn("mock-jwt-token");

        AuthResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("newuser");
        request.setPassword("pass123");
        request.setRole("USER");
        request.setDni("1234567890");

        Role role = new Role();
        role.setId(java.util.UUID.randomUUID());
        role.setName("USER");

        when(usersRepository.existsByUsername("newuser")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass123")).thenReturn("hashed-pass");
        
        Person person = new Person();
        person.setId(java.util.UUID.randomUUID());
        when(personRepository.save(any(Person.class))).thenReturn(person);

        authService.register(request);

        verify(usersRepository).save(any(User.class));
    }
}
