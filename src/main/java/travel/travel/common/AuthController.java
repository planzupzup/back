package travel.travel.common;

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
public class AuthController {

    private final AuthService authService;

    @GetMapping
    public ResponseEntity<CommonResDto<String>> validLogin() {
        String dto = authService.isAuthenticatedUser() ? "로그인 성공" : "로그인 실패";
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "로그인 여부 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }
}
