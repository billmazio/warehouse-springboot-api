package gr.clothesmanager.auth.dto;

import lombok.Data;

@Data
public class SetupRequestDTO {
    private String username;
    private String password;
    private String storeTitle;
    private String storeAddress;
}