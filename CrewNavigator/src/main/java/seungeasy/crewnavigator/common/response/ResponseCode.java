package seungeasy.crewnavigator.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * <pre>
 * Class Name: ResponseCode
 * Description: API 응답 상태 코드 및 메시지 정의
 * Example:
 *      // 성공 시
 *      ResponseCode.OK.getCode() // "S001"
 *      ResponseCode.OK.getMessage() // "요청이 성공적으로 처리되었습니다."
 *
 *      // 예외 발생 시 (BusinessException과 함께 사용)
 *      throw new BusinessException(ResponseCode.USER_NOT_FOUND);
 *
 * History
 * 2024/06/04  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // ---------------------------------------------
    // 성공 코드
    // ---------------------------------------------
    /**
     * 일반적인 성공
     *
     * 상황: GET 요청으로 데이터를 성공적으로 조회했을 때. 가장 기본이 되는 성공 코드
     * HTTP 상태: 200 Ok
     */
    OK(HttpStatus.OK, "S001", "요청이 성공적으로 처리되었습니다."),

    /**
     * 생성 성공
     *
     * 상황: POST 요청으로 새로운 리소스(회원, 게시글 등)를 성공적으로 만들었을 때
     * HTTP 상태: 201 Created (200보다 더 명확한 의미 전달)
     */
    CREATED(HttpStatus.CREATED, "S001", "리소스가 성공적으로 생성되었습니다."),

    /**
     * 성공했으나 반환할 내용 없음
     *
     * 상황: DELETE 요청으로 데이터를 성공적으로 삭제했거나, 업데이트 후 굳이 다시 객체를 반환할 필요가 없을 때
     * HTTP 상태: 204 No Content (응답 본문(body)가 비어야 함)
     */
    NO_CONTENT(HttpStatus.NO_CONTENT, "S001", "요청은 성공했으나, 반환할 데이터가 없습니다."),

    /**
     * 요청 접수됨
     *
     * 상황: 처리하는 데 시간이 오래 걸리거나 작업을 비동기로 처리하도록 요청했을 때. "알았어, 요청은 받았고 처리 시작할게!" 라는 의미
     * HTTP 상태: 202 Accepted
     */
    ACCEPTED(HttpStatus.ACCEPTED, "S004", "요청이 접수되었으며, 처리가 진행 중입니다."),

    // ---------------------------------------------
    // 에러 코드
    // ---------------------------------------------

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "EC001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EC002", "서버 내부 오류가 발생했습니다."),

    // 계정/인증 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A002", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "A003", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "A004", "이미 사용 중인 이메일입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A006", "만료된 토큰입니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "A007", "로그아웃 또는 강제 로그아웃된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A008", "리프레시 토큰을 찾을 수 없습니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "A009", "계정이 잠겼습니다."),
    ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "A010", "비활성화된 계정입니다."),
    ACCOUNT_LEAVE(HttpStatus.GONE, "A011", "탈퇴한 계정입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "A012", "인증되지 않은 접근입니다."),
    FORCE_LOGOUT(HttpStatus.UNAUTHORIZED, "A013", "관리자에 의해 강제 로그아웃되었습니다.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String code;           // 비즈니스 커스텀 코드
    private final String message;        // 사용자에게 보여줄 메시지
}
