package gr.clothesmanager.auth.dto;

import gr.clothesmanager.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String username;
    private Set<UserRole> roles;


}
