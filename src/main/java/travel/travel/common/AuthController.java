package travel.travel.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.service.AuthService;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관리 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인 상태 확인", description = "사용자의 로그인 상태를 확인합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 상태 확인 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class)))
    @GetMapping
    public ResponseEntity<CommonResDto<String>> validLogin() {
        String dto = authService.isAuthenticatedUser() ? "로그인 성공" : "로그인 실패";
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "로그인 여부 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }
}
