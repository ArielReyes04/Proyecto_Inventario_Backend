package ec.edu.espe.master.config;

import ec.edu.espe.master.dto.RegisterRequestDto;
import ec.edu.espe.master.entity.Role;
import ec.edu.espe.master.entity.Category;
import ec.edu.espe.master.entity.Customer;
import ec.edu.espe.master.entity.Product;
import ec.edu.espe.master.entity.Provider;
import ec.edu.espe.master.repository.RoleRepository;
import ec.edu.espe.master.repository.UsersRepository;
import ec.edu.espe.master.repository.CategoryRepository;
import ec.edu.espe.master.repository.CustomerRepository;
import ec.edu.espe.master.repository.ProductRepository;
import ec.edu.espe.master.repository.ProviderRepository;
import ec.edu.espe.master.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsersRepository usersRepository;
    private final AuthService authService;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Value("${app.defaults.admin-password:admin1234}")
    private String adminPassword;

    @Value("${app.defaults.bodega-password:bodega1234}")
    private String bodegaPassword;

    @Value("${app.defaults.vendedor-password:vendedor1234}")
    private String vendedorPassword;

    @Value("${app.defaults.asistente-password:asistente1234}")
    private String asistentePassword;

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
        createUserIfNotExists("admin", adminPassword, "Administrador", "0000000001", "Admin", "I", "System");
        createUserIfNotExists("bodega", bodegaPassword, "Encargado de Bodega", "0000000002", "Encargado", "B", "Bodega");
        createUserIfNotExists("vendedor", vendedorPassword, "Vendedor", "0000000003", "Vendedor", "V", "Ventas");
        createUserIfNotExists("asistente", asistentePassword, "Asistente de Compras", "0000000004", "Asistente", "A", "Compras");

        // 3. Crear Categorías
        Category catHerramientas = createCategoryIfNotExists("Herramientas Manuales", "Alicates, martillos, desarmadores, etc.");
        createCategoryIfNotExists("Material Eléctrico", "Cables, tomacorrientes, focos, etc.");

        // 4. Crear Proveedor
        Provider provHolcim = createProviderIfNotExists("0999999999001", "Holcim Ecuador S.A.", "contacto@holcim.com", "0999999991", "Guayaquil");

        // 5. Crear Cliente
        createCustomerIfNotExists("1712345678", "Juan Perez", "juanperez@gmail.com", "0987654321", "Quito");

        // 6. Crear Productos
        if (catHerramientas != null && provHolcim != null) {
            createProductIfNotExists("SKU-MART-001", "Martillo Truper 16oz", "Martillo de uña curva", "Bodega A - Pasillo 1", 
                                     new BigDecimal("5.50"), new BigDecimal("8.00"), 10, catHerramientas, provHolcim);
        }

        log.info("Inicialización de datos completada.");
    }

    private Category createCategoryIfNotExists(String name, String desc) {
        if (!categoryRepository.existsByName(name)) {
            Category cat = Category.builder().name(name).description(desc).active(true).build();
            return categoryRepository.save(cat);
        }
        return categoryRepository.findByName(name).orElse(null);
    }

    private Provider createProviderIfNotExists(String doc, String name, String email, String phone, String address) {
        if (!providerRepository.existsByDocumentNumber(doc)) {
            Provider prov = Provider.builder().documentNumber(doc).name(name).email(email).phoneNumber(phone).address(address).active(true).build();
            return providerRepository.save(prov);
        }
        return providerRepository.findByDocumentNumber(doc).orElse(null);
    }

    private void createCustomerIfNotExists(String doc, String name, String email, String phone, String address) {
        if (!customerRepository.existsByDocumentNumber(doc)) {
            Customer cust = Customer.builder().documentNumber(doc).name(name).email(email).phoneNumber(phone).address(address).active(true).build();
            customerRepository.save(cust);
        }
    }

    private void createProductIfNotExists(String sku, String name, String desc, String loc, BigDecimal cost, BigDecimal sale, int minStock, Category cat, Provider prov) {
        if (!productRepository.existsBySku(sku)) {
            Product prod = Product.builder().sku(sku).name(name).description(desc).location(loc).costPrice(cost).salePrice(sale).minimumStock(minStock).currentStock(50).category(cat).provider(prov).active(true).build();
            productRepository.save(prod);
            log.info("Producto '{}' creado con 50 unidades de stock inicial.", sku);
        }
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
