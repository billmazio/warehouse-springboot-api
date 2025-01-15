package gr.clothesmanager.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessageDTO {
    private String status;
    private String message;
}
