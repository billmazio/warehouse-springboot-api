package gr.clothesmanager.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseMessageDTO {
    private String status;
    private String message;
}
