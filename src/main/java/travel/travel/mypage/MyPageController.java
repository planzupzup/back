package travel.travel.mypage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel.common.dto.CommonErrorDto;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.dto.PageApiResDto;
import travel.travel.common.service.AuthService;
import travel.travel.plan.domain.PlanSortType;
import travel.travel.plan.dto.PlanThumbResDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/my-page")
@Tag(name = "MyPage", description = "마이페이지 관리 API")
public class MyPageController {

    private final MyPageService myPageService;
    private final AuthService authService;

    @Operation(summary = "내 정보 수정", description = "내 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 정보 수정 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "프로필 이미지 업로드 중 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @PutMapping
    public ResponseEntity<CommonResDto<MemberResDto>> updateMyInfo(
            @Parameter(description = "수정할 사용자 정보") @Valid @RequestPart MemberReqDto memberReqDto,
            @Parameter(description = "프로필 이미지 파일") @RequestPart(required = false) MultipartFile file) {
        Long memberId = authService.getAuthenticatedUserId();
        MemberResDto dto = myPageService.updateMyInfo(memberReqDto, file, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내 정보 변경이 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping
    public ResponseEntity<CommonResDto<MemberResDto>> getMyInfo() {
        Long memberId = authService.getAuthenticatedUserId();
        MemberResDto dto = myPageService.getMyInfo(memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내 정보 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "북마크한 계획 목록 조회", description = "내가 북마크한 계획 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "북마크한 계획 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/bookmark/{type}")
    public ResponseEntity<CommonResDto<PageApiResDto<PlanThumbResDto>>> getBookmarkedPlans(
            @Parameter(description = "정렬 타입 (LATEST: 최신순, COMMENT: 댓글순, BOOKMARK: 북마크순)") @PathVariable PlanSortType type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = authService.getAuthenticatedUserId();
        PageApiResDto<PlanThumbResDto> dto = myPageService.getBookmarkedPlans(type, page, size, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크한 계획 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "내가 작성한 계획 목록 조회", description = "내가 작성한 계획 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 작성한 계획 조회 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @GetMapping("/plans")
    public ResponseEntity<CommonResDto<PageApiResDto<PlanThumbResDto>>> getMyPlans(
            @Parameter(description = "공개/비공개 설정 필터 (ALL: 전체, PUBLIC: 공개글, PRIVATE: 비공개글)") @RequestParam(defaultValue = "ALL") VisibilityType visibility,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        Long memberId = authService.getAuthenticatedUserId();
        PageApiResDto<PlanThumbResDto> dto = myPageService.getMyPlans(visibility, page, size, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "내가 쓴 글 목록 조회가 성공적으로 되었습니다.", dto), HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = @Content(schema = @Schema(implementation = CommonResDto.class))),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonErrorDto.class)))
    })
    @DeleteMapping
    public ResponseEntity<CommonResDto<MemberResDto>> deleteMyInfo() {
        Long memberId = authService.getAuthenticatedUserId();
        MemberResDto dto = myPageService.deleteMyInfo(memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "탈퇴합니다.", dto), HttpStatus.OK);
    }
}
