package travel.travel.mypage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResponse;
import travel.travel.common.service.AuthService;
import travel.travel.plan.domain.PlanSortType;
import travel.travel.plan.dto.PlanThumbResDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/my-page")
public class MyPageController {

    private final MyPageService myPageService;
    private final AuthService authService;

    @PutMapping
    public ResponseEntity<CommonResDto<MemberResDto>> updateMyInfo(
            @Valid @RequestPart MemberReqDto memberReqDto,
            @RequestPart(required = false) MultipartFile file) {
        Long memberId = authService.getAuthenticatedUserId();
        MemberResDto dto = myPageService.updateMyInfo(memberReqDto, file, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내 정보 변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResDto<MemberResDto>> getMyInfo() {
        Long memberId = authService.getAuthenticatedUserId();
        MemberResDto dto = myPageService.getMyInfo(memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내 정보 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/bookmark/{type}")
    public ResponseEntity<CommonResDto<PageApiResponse<PlanThumbResDto>>> getBookmarkedPlans(
            @PathVariable PlanSortType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = authService.getAuthenticatedUserId();
        PageApiResponse<PlanThumbResDto> dto = myPageService.getBookmarkedPlans(type, page, size, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크한 계획 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @GetMapping("/plans")
    public ResponseEntity<CommonResDto<PageApiResponse<PlanThumbResDto>>> getMyPlans(
            @RequestParam(defaultValue = "ALL") VisibilityType visibility,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = authService.getAuthenticatedUserId();
        PageApiResponse<PlanThumbResDto> dto = myPageService.getMyPlans(visibility, page, size, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내가 쓴 글 목록 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<CommonResDto<MemberResDto>> deleteMyInfo() {
        Long memberId = authService.getAuthenticatedUserId();
        MemberResDto dto = myPageService.deleteMyInfo(memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "탈퇴합니다.", dto), HttpStatus.OK);
    }
}
