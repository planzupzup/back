package travel.travel.mypage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.plan.dto.PlanThumbResDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/my-page")
public class MyPageController {

    private final MyPageService myPageService;

    @PutMapping("/nickname")
    public ResponseEntity<CommonResDto<String>> updateNickName(
            @Valid @RequestBody NickNameReqDto nickNameReqDto) {
        String dto = myPageService.updateNickName(nickNameReqDto);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "닉네임 변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/bookmark")
    public ResponseEntity<CommonResDto<PageApiResponse<PlanThumbResDto>>> getBookmarkedPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageApiResponse<PlanThumbResDto> dto = myPageService.getBookmarkedPlans(page, size);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }
}
