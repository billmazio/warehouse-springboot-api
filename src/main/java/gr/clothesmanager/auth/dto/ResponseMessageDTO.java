package gr.clothesmanager.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessageDTO {
    private String status;
    private String message;
}
