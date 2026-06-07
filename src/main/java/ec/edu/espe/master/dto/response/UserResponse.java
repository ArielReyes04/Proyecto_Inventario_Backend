package ec.edu.espe.master.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private String id;
    private String username;
    private PersonResponse person;
    private Boolean active;
    private List<String> roles;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}
