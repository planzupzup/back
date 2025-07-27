package travel.travel.bookmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.bookmark.service.BookmarkService;
import travel.travel.common.dto.CommonResDto;
import travel.travel.common.service.AuthService;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/plan")
public class BookmarkController {
    
    private final BookmarkService bookmarkService;
    private final AuthService authService;

    @PostMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto<Long>> addBookmark (@PathVariable Long planId) {
        Long memberId = authService.getAuthenticatedUserId();
        bookmarkService.addBookmark(planId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크추가가 성공적으로 되었습니다.",planId ), HttpStatus.OK);
    }

    @DeleteMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto<Long>> removeBookmark(@PathVariable Long planId) {
        Long memberId = authService.getAuthenticatedUserId();
        bookmarkService.removeBookmark(planId, memberId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크취소가 성공적으로 되었습니다.", planId), HttpStatus.OK);
    }

    @GetMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto<Long>> getBookmarkCount(@PathVariable Long planId) {
        Long bookmarkCount = bookmarkService.getBookmarkCount(planId);
        return new ResponseEntity<>(CommonResDto.of(HttpStatus.OK, "북마크개수조회가 성공적으로 되었습니다.", bookmarkCount), HttpStatus.OK);
    }
}
