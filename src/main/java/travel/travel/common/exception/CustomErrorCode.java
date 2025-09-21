package travel.travel.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "계획을 찾을 수 없습니다."),
    DESTINATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장소입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "시작일은 종료일보다 이전이어야 합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근할 권한이 없습니다."),
    UPDATE_DENIED(HttpStatus.FORBIDDEN, "수정할 권한이 없습니다."),
    DELETE_DENIED(HttpStatus.FORBIDDEN, "삭제할 권한이 없습니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}