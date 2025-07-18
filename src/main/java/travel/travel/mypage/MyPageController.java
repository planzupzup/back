package travel.travel.mypage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.plan.dto.PlanThumbResDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/my-page")
public class MyPageController {

    private final MyPageService myPageService;

    @PutMapping
    public ResponseEntity<CommonResDto<MemberResDto>> updateMyInfo(
            @Valid @RequestPart NickNameReqDto nickNameReqDto,
            @RequestPart(required = false) MultipartFile file) {
        MemberResDto dto = myPageService.updateMyInfo(nickNameReqDto, file);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내 정보 변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResDto<MemberResDto>> getMyInfo() {
        MemberResDto dto = myPageService.getMyInfo();
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내 정보 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/bookmark")
    public ResponseEntity<CommonResDto<PageApiResponse<PlanThumbResDto>>> getBookmarkedPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageApiResponse<PlanThumbResDto> dto = myPageService.getBookmarkedPlans(page, size);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "계획목록조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/plans")
    public ResponseEntity<CommonResDto<PageApiResponse<PlanThumbResDto>>> getMyPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageApiResponse<PlanThumbResDto> dto = myPageService.getMyPlans(page, size);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내가 쓴 글 목록 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<CommonResDto<MemberResDto>> deleteMyInfo() {
        MemberResDto dto = myPageService.deleteMyInfo();
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "탈퇴합니다.", dto), HttpStatus.OK);
    }
}
