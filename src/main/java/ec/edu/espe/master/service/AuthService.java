package ec.edu.espe.master.service;

import ec.edu.espe.master.dto.AuthRequestDto;
import ec.edu.espe.master.dto.AuthResponseDto;
import ec.edu.espe.master.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto login(AuthRequestDto request);
    void register(RegisterRequestDto request);
}
