package ec.edu.espe.master.services;


import ec.edu.espe.master.dto.request.UserRequest;
import ec.edu.espe.master.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(UUID id, UserRequest userRequest);
    void deleteUser(UUID userID);
    UserResponse toggleActive(UUID userID);
    UserResponse getUserById(UUID id);
    UserResponse assigneRole(UUID userId, UUID roleId);
    UserResponse removeRole(UUID userId, UUID roleId);
    UserResponse getCurrentUserProfile(String username);
    UserResponse updateCurrentUserProfile(String username, ec.edu.espe.master.dto.request.ProfileUpdateRequest request);
    void changePassword(String username, ec.edu.espe.master.dto.request.PasswordChangeRequest request);
}
