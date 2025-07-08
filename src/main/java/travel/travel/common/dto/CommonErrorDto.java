package travel.travel.common.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonErrorDto {
    private int status_code;
    private String error_message;

    public static CommonErrorDto of(HttpStatus status, String message) {
        return CommonErrorDto.builder()
                .status_code(status.value())
                .error_message(message)
                .build();
    }
}
