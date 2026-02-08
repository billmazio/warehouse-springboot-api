package gr.clothesmanager.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Το πεδίο είναι υποχρεωτικό")
    @Size(min = 3, max = 50, message = "Το όνομα χρήστη πρέπει να είναι από 3 έως 50 χαρακτήρες.")
    private String username;

    @NotBlank(message = "Το πεδίο είναι υποχρεωτικό")
    @Size(min = 6, message = "Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες.")
    private String password;

}