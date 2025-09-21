package travel.travel.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 계획을 찾을 수 없습니다."),
    DESTINATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이미지를 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 지역을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이전이어야 합니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),
    NO_FILES_TO_UPLOAD(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다."),
    MAX_COMMENT_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "대댓글(2단계)까지만 작성할 수 있습니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근할 권한이 없습니다."),
    UPDATE_DENIED(HttpStatus.FORBIDDEN, "수정할 권한이 없습니다."),
    DELETE_DENIED(HttpStatus.FORBIDDEN, "삭제할 권한이 없습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 업로드 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}