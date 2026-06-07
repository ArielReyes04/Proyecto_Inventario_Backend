package ec.edu.espe.master.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    @Size(max = 10, message = "El DNI no puede tener mas de 10 caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "El DNI solo puede contener números")
    private String dni;

    @NotBlank
    @Size(max = 30, message = "El nombre no puede tener mas de 30 caracteres")
    @Pattern(regexp = "^[a-z A-Z]+$", message = "El nombre solo puede contener letras")
    private String firstName;

    @Size(max = 30, message = "El nombre no puede tener mas de 30 caracteres")
    @Pattern(regexp = "^[a-z A-Z]+$", message = "El nombre solo puede contener letras")
    private String middleName;

    @Size(max = 30, message = "El apellido no puede tener mas de 30 caracteres")
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "El correo electrónico no es válido")
    @Size(max = 50, message = "El correo electrónico no puede tener más de 50 caracteres")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo puede contener números")
    @Size(max = 10, message = "El teléfono no puede tener mas de 10 caracteres")
    private String phoneNumber;

    @NotBlank(message = "El campo dirección es obligatorio")
    private String address;

    @NotBlank(message = "El campo nacionalidad es obligatorio")
    private String nationality;

}
