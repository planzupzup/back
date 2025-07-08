package travel.travel.common.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommonResDto<T> {
    private int statusCode;
    private String statusMessage;
    private T result;

    public static <T> CommonResDto<T> of(HttpStatus status, String message, T result) {
        return CommonResDto.<T>builder()
                .statusCode(status.value())
                .statusMessage(message)
                .result(result)
                .build();
    }
}
