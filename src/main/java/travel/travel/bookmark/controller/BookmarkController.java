package travel.travel.bookmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel.bookmark.service.BookmarkService;
import travel.travel.common.dto.CommonResDto;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/plan")
public class BookmarkController {
    
    private final BookmarkService bookmarkService;


    @PostMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto> addBookmark (@PathVariable Long planId) {
        bookmarkService.addBookmark(planId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "북마크추가가 성공적으로 되었습니다.", null), HttpStatus.OK);
    }

    @DeleteMapping("/{planId}/bookmark")
    public ResponseEntity<CommonResDto> removeBookmark(@PathVariable Long planId) {
        bookmarkService.removeBookmark(planId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "북마크취소가 성공적으로 되었습니다.", null), HttpStatus.OK);
    }

    @GetMapping("/{planId}/bookmark/count")
    public ResponseEntity<CommonResDto> getBookmarkCount(@PathVariable Long planId) {
        Long bookmarkCount = bookmarkService.getBookmarkCount(planId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "북마크개수조회가 성공적으로 되었습니다.", bookmarkCount), HttpStatus.OK);
    }
}
