package ec.edu.espe.master.security;

import ec.edu.espe.master.entity.User;
import ec.edu.espe.master.entity.UserRole;
import ec.edu.espe.master.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * UserDetailsServiceImpl - Implementación del servicio de detalles de usuario.
 * 
 * Interfaz requerida por Spring Security para cargar usuarios de BD.
 * Se utiliza durante el proceso de autenticación para:
 * 1. Buscar el usuario por username
 * 2. Cargar sus datos (username, password hash, authorities/roles)
 * 3. Construir un objeto UserDetails para validación
 * 
 * Método principal: loadUserByUsername()
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repositorio para acceder a usuarios de BD.
     */
    private final UsersRepository usersRepository;

    /**
     * Carga los detalles de un usuario por su username.
     * 
     * Implementación de UserDetailsService para Spring Security.
     * Se llama durante:
     * - Autenticación (login)
     * - Validación de tokens JWT
     * - Verificación de autoridades/permisos
     * 
     * Flujo:
     * 1. Busca el usuario en BD por username
     * 2. Si no existe, lanza UsernameNotFoundException
     * 3. Extrae los roles activos del usuario
     * 4. Convierte los roles a GrantedAuthority
     * 5. Crea y retorna un UserDetails de Spring Security
     * 
     * @param username El nombre de usuario a buscar
     * @return UserDetails con username, password hash y authorities
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en BD
        User user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // Extrae los roles activos y los convierte a GrantedAuthority
        Collection<GrantedAuthority> authorities = user.getUserRoles().stream()
                .filter(UserRole::getActive)  // Solo incluye roles activos
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
                .collect(Collectors.toList());

        // Retorna un UserDetails con:
        // - username: para validación de identidad
        // - passwordHash: para validación de credenciales
        // - authorities: para validación de permisos
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }
}
