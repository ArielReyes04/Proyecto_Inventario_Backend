package ec.edu.espe.master.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerResponse {
    private UUID id;
    private String documentNumber;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
