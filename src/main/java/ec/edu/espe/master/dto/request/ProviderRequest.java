package ec.edu.espe.master.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import ec.edu.espe.master.validator.EcuadorianId;

@Data
public class ProviderRequest {
    @NotBlank(message = "El RUC o documento es obligatorio")
    @Size(max = 13, message = "El documento no puede tener mas de 13 caracteres")
    @EcuadorianId
    private String documentNumber;

    @NotBlank(message = "El nombre o razón social es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
    private String name;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "El correo electrónico no es válido")
    @Size(max = 50, message = "El correo electrónico no puede tener más de 50 caracteres")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo puede contener números")
    @Size(min = 9, max = 20, message = "El teléfono debe tener entre 9 y 20 caracteres")
    private String phoneNumber;

    @NotBlank(message = "El campo dirección es obligatorio")
    private String address;
}
