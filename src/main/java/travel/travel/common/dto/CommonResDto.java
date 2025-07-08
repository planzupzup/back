package travel.travel.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommonResDto {
    private int status_code;
    private String Status_message;
    private Object result;

    public static CommonResDto of(HttpStatus status, String message, Object result) {
        return CommonResDto.builder()
                  .status_code(status.value())
                  .Status_message(message)
                  .result(result)
                  .build();
    }
}
