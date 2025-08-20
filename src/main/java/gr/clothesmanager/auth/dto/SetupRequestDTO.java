package gr.clothesmanager.auth.dto;

import gr.clothesmanager.core.enums.Status;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class SetupRequestDTO {
    private String username;
    private String password;
    private String storeTitle;
    private String storeAddress;
}