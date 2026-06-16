package ec.edu.espe.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MasterApplication - Clase principal de la aplicación Spring Boot.
 * 
 * Esta es la clase de inicio de la aplicación de gestión de inventario.
 * Utiliza la anotación @SpringBootApplication que combina:
 * - @SpringBootConfiguration: Indica que es una clase de configuración de Spring Boot
 * - @EnableAutoConfiguration: Activa la configuración automática basada en las dependencias
 * - @ComponentScan: Habilita el escaneo de componentes en el paquete base y sus sub-paquetes
 * 
 * La aplicación inicia el contenedor Spring y realiza todo el setup necesario
 * para que funcione el backend de inventario con autenticación JWT.
 */
@SpringBootApplication
public class MasterApplication {

	/**
	 * Método main - Punto de entrada de la aplicación.
	 * 
	 * @param args Argumentos de línea de comandos (generalmente vacío)
	 */
	public static void main(String[] args) {
		SpringApplication.run(MasterApplication.class, args);
	}

}
