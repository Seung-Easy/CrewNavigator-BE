package seungeasy.crewnavigator.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * <pre>
 * Class Name: ResponseCode
 * Description: API 응답 상태 코드 및 메시지 정의
 *
 * 코드 체계:
 * - 성공: S + 일련번호 (예: S001 - OK, S002 - Created, S003 - No Content)
 * - 에러: E + 도메인이니셜(대문자) + 일련번호 (예: EC001 - Common, EA001 - Auth, EP001 - Post, EM001 - Comment)
 *
 * Example:
 * // 성공 시
 * ResponseCode.OK.getCode() // "S001"
 * ResponseCode.OK.getMessage() // "요청이 성공적으로 처리되었습니다."
 *
 * // 예외 발생 시 (BusinessException과 함께 사용)
 * throw new BusinessException(ResponseCode.USER_NOT_FOUND);
 *
 * History
 * 2024/06/04  Seung-Geon: Class 생성 및 Javadoc 추가
 * 2024/06/12  Seung-Geon: 코드 체계 변경 (S001 중복 제거, 에러 코드 E prefix 도입)
 * 2026.06.15: Seung-Geon: EA017~EA020 이메일 인증 관련 에러 코드 추가
 * 2026.06.22: Seung-Geon: EA013 FORCE_LOGOUT 메시지에 계정 잠김/비밀번호 재설정 안내 추가
 * 2026.06.27: Chi-Yoon: EP001 CATEGORY_NOT_FOUND 게시글 카테고리 누락 에러 코드 추가
 * 2026.07.01: Chi-Yoon: EP002 NOT_POST_WRITER 게시글 수정/삭제 권한 부족 에러 코드 추가
 * 2026.07.05: Chi-Yoon: EM001 NOT_COMMENT_WRITER 댓글 수정/삭제 권한 부족 에러 코드 추가
 * 2026.07.05: Chi-Yoon: EM002 COMMENT_NOT_FOUND 존재하지 않는 댓글 에러 코드 추가 (💡 추가)
 * </pre>
 *
 * @author Seung-Geon, Chi-Yoon
 * @version 1.6
 */
@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // =============================================
    // 성공 코드: S + 일련번호
    // =============================================
    /**
     * 요청이 성공적으로 처리되었습니다.
     *
     * 상황: GET 요청으로 데이터를 성공적으로 조회했을 때. 가장 기본이 되는 성공 코드
     * HTTP 상태: 200 Ok
     */
    OK(HttpStatus.OK, "S001", "요청이 성공적으로 처리되었습니다."),

    /**
     * 리소스가 성공적으로 생성되었습니다.
     *
     * 상황: POST 요청으로 새로운 리소스(회원, 게시글 등)를 성공적으로 만들었을 때
     * HTTP 상태: 201 Created
     */
    CREATED(HttpStatus.CREATED, "S002", "리소스가 성공적으로 생성되었습니다."),

    /**
     * 요청은 성공했으나, 반환할 데이터가 없습니다.
     *
     * 상황: DELETE 요청으로 데이터를 성공적으로 삭제했거나, 업데이트 후 굳이 다시 객체를 반환할 필요가 없을 때
     * HTTP 상태: 204 No Content
     */
    NO_CONTENT(HttpStatus.NO_CONTENT, "S003", "요청은 성공했으나, 반환할 데이터가 없습니다."),

    /**
     * 요청이 접수되었으며, 처리가 진행 중입니다.
     *
     * 상황: 처리하는 데 시간이 오래 걸리거나 작업을 비동기로 처리하도록 요청했을 때
     * HTTP 상태: 202 Accepted
     */
    ACCEPTED(HttpStatus.ACCEPTED, "S004", "요청이 접수되었으며, 처리가 진행 중입니다."),

    // =============================================
    // 에러 코드: E + 도메인 이니셜(대문자) + 일련번호
    // =============================================

    // ------- EC: 공통 (Common) -------
    /**
     * 잘못된 입력값입니다.
     *
     * 상황: 요청 파라미터, Request Body의 Validation 실패 시
     * HTTP 상태: 400 Bad Request
     */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "EC001", "잘못된 입력값입니다."),

    /**
     * 서버 내부 오류가 발생했습니다.
     *
     * 상황: 예상치 못한 서버 내부 예외 발생 시 (500으로 통일)
     * HTTP 상태: 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EC002", "서버 내부 오류가 발생했습니다."),

    // ------- EA: 인증/계정 (Auth) -------
    /**
     * 사용자를 찾을 수 없습니다.
     *
     * 상황: userId 또는 userNo로 조회했을 때 존재하지 않는 사용자
     * HTTP 상태: 404 Not Found
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EA001", "사용자를 찾을 수 없습니다."),

    /**
     * 비밀번호가 일치하지 않습니다.
     *
     * 상황: 로그인 시 비밀번호가 틀렸을 때 (계정 존재 여부는 노출하지 않으므로 USER_NOT_FOUND 대신 이 코드 사용 가능)
     * HTTP 상태: 401 Unauthorized
     */
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "EA002", "비밀번호가 일치하지 않습니다."),

    /**
     * 이미 사용 중인 아이디입니다.
     *
     * 상황: 회원가입 시 이미 존재하는 userId로 가입 요청할 때
     * HTTP 상태: 409 Conflict
     */
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "EA003", "이미 사용 중인 아이디입니다."),

    /**
     * 이미 사용 중인 이메일입니다.
     *
     * 상황: 회원가입 또는 이메일 변경 시 이미 존재하는 이메일로 요청할 때
     * HTTP 상태: 409 Conflict
     */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "EA004", "이미 사용 중인 이메일입니다."),

    /**
     * 유효하지 않은 토큰입니다.
     *
     * 상황: JWT 서명이 위조되었거나, 형식이 잘못된 토큰으로 요청할 때
     * HTTP 상태: 401 Unauthorized
     */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "EA005", "유효하지 않은 토큰입니다."),

    /**
     * 만료된 토큰입니다.
     *
     * 상황: accessToken의 유효기간이 지난 경우. 클라이언트는 refreshToken으로 재발급 요청해야 함
     * HTTP 상태: 401 Unauthorized
     */
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EA006", "만료된 토큰입니다."),

    /**
     * 로그아웃 또는 강제 로그아웃된 토큰입니다.
     *
     * 상황: 사용자가 로그아웃했거나 관리자에 의해 강제 로그아웃되어 Redis에 블랙리스트 등록된 토큰
     * HTTP 상태: 401 Unauthorized
     */
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "EA007", "로그아웃 또는 강제 로그아웃된 토큰입니다."),

    /**
     * 리프레시 토큰을 찾을 수 없습니다.
     *
     * 상황: refreshToken이 없거나, Redis에 저장된 refreshToken과 일치하지 않을 때
     * HTTP 상태: 401 Unauthorized
     */
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "EA008", "리프레시 토큰을 찾을 수 없습니다."),

    /**
     * 계정이 잠겼습니다.
     *
     * 상황: 로그인 실패 횟수 초과로 계정이 잠겼을 때
     * HTTP 상태: 403 Forbidden
     */
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "EA009", "계정이 잠겼습니다."),

    /**
     * 비활성화된 계정입니다.
     *
     * 상황: 관리자에 의해 계정이 비활성화(disabled)된 상태에서 접근할 때
     * HTTP 상태: 403 Forbidden
     */
    ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "EA010", "비활성화된 계정입니다."),

    /**
     * 탈퇴한 계정입니다.
     *
     * 상황: 회원 탈퇴(soft delete)한 계정으로 접근할 때
     * HTTP 상태: 410 Gone
     */
    ACCOUNT_LEAVE(HttpStatus.GONE, "EA011", "탈퇴한 계정입니다."),

    /**
     * 인증되지 않은 접근입니다.
     *
     * 상황: 토큰 없이 인증이 필요한 API에 접근할 때 (AuthenticationEntryPoint fallback)
     * HTTP 상태: 401 Unauthorized
     */
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "EA012", "인증되지 않은 접근입니다."),

    /**
     * 관리자에 의해 강제 로그아웃되었습니다. 계정이 잠겼습니다.
     *
     * 상황: 관리자가 특정 계정을 강제 로그아웃 처리했을 때.
     * 계정이 잠겼으므로 비밀번호 재설정(이메일 인증) 후에 다시 로그인할 수 있습니다.
     * HTTP 상태: 401 Unauthorized
     */
    FORCE_LOGOUT(HttpStatus.UNAUTHORIZED, "EA013", "관리자에 의해 강제 로그아웃되었습니다. 계정이 잠겼습니다. 비밀번호 재설정 후 로그인해주세요."),

    /**
     * 아이디 또는 비밀번호가 일치하지 않습니다.
     *
     * 상황: 로그인 시 인증 정보가 올바르지 않을 때 (계정 존재 여부 노출 방지를 위해 USER_NOT_FOUND/INVALID_PASSWORD 대신 통일)
     * HTTP 상태: 401 Unauthorized
     */
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "EA014", "아이디 또는 비밀번호가 일치하지 않습니다."),

    /**
     * 계정이 만료되었습니다.
     *
     * 상황: 사용자 계정의 유효기간이 지난 경우
     * HTTP 상태: 403 Forbidden
     */
    ACCOUNT_EXPIRED(HttpStatus.FORBIDDEN, "EA015", "계정이 만료되었습니다."),

    /**
     * 비밀번호가 만료되었습니다.
     *
     * 상황: 비밀번호 변경 주기가 지나 새 비밀번호로 변경이 필요한 경우
     * HTTP 상태: 401 Unauthorized
     */
    PASSWORD_EXPIRED(HttpStatus.UNAUTHORIZED, "EA016", "비밀번호가 만료되었습니다."),

    /**
     * 이메일 인증이 필요합니다.
     *
     * 상황: 이메일 인증이 완료되지 않은 상태에서 접근할 때
     * HTTP 상태: 403 Forbidden
     */
    EMAIL_VERIFICATION_REQUIRED(HttpStatus.FORBIDDEN, "EA017", "이메일 인증이 필요합니다."),

    /**
     * 인증코드가 올바르지 않습니다.
     *
     * 상황: 입력한 이메일 인증코드가 일치하지 않을 때
     * HTTP 상태: 400 Bad Request
     */
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "EA018", "인증코드가 올바르지 않습니다."),

    /**
     * 인증코드가 만료되었습니다.
     *
     * 상황: 이메일 인증코드의 유효시간이 초과되었을 때
     * HTTP 상태: 400 Bad Request
     */
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "EA019", "인증코드가 만료되었습니다."),

    /**
     * 이미 인증된 이메일입니다.
     *
     * 상황: 이미 인증이 완료된 이메일로 다시 인증을 시도할 때
     * HTTP 상태: 409 Conflict
     */
    EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "EA020", "이미 인증된 이메일입니다."),

    /**
     * 최근 사용한 비밀번호로 변경할 수 없습니다.
     *
     * 상황: 비밀번호 변경/재설정 시 최근 3회 이내에 사용한 비밀번호와 동일한 비밀번호로 요청할 때
     * HTTP 상태: 400 Bad Request
     */
    RECENTLY_USED_PASSWORD(HttpStatus.BAD_REQUEST, "EA021", "최근 사용한 비밀번호로 변경할 수 없습니다."),

    /**
     * 현재 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.
     *
     * 상황: 비밀번호 변경/재설정 시 현재 사용 중인 비밀번호와 동일한 비밀번호로 요청할 때
     * HTTP 상태: 400 Bad Request
     */
    SAME_AS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "EA022", "현재 비밀번호와 동일한 비밀번호로 요청할 때."),


    // ------- EP: 게시글/카테고리 (Post) -------
    /**
     * 존재하지 않는 카테고리입니다.
     *
     * 상황: 게시글 작성/수정 시 request로 넘어온 categoryId에 해당하는 카테고리가 DB에 없을 때
     * HTTP 상태: 404 Not Found
     */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "EP001", "존재하지 않는 카테고리입니다."),

    /**
     * 게시글 수정/삭제 권한이 없습니다.
     *
     * 상황: 로그인한 유저가 자신이 작성하지 않은 타인의 게시글을 수정하거나 삭제하려고 시도할 때
     * HTTP 상태: 403 Forbidden
     */
    NOT_POST_WRITER(HttpStatus.FORBIDDEN, "EP002", "해당 게시글의 작성자가 아닙니다."),


    // ------- EM: 댓글 (Comment) -------
    /**
     * 댓글 수정/삭제 권한이 없습니다.
     *
     * 상황: 로그인한 유저가 자신이 작성하지 않은 타인의 댓글을 수정하거나 삭제하려고 시도할 때
     * HTTP 상태: 403 Forbidden
     */
    NOT_COMMENT_WRITER(HttpStatus.FORBIDDEN, "EM001", "해당 댓글의 작성자가 아닙니다."),

    /**
     * 존재하지 않는 댓글입니다.
     *
     * 상황: 댓글 수정/삭제 시 해당 ID의 댓글이 데이터베이스에 존재하지 않거나 이미 삭제되었을 때
     * HTTP 상태: 404 Not Found
     */
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EM002", "존재하지 않는 댓글입니다."), // 💡 추가됨


    // ------- EG: 그룹 (Group) -------
    /**
     * 존재하지 않는 그룹입니다.
     *
     * 상황: 그룹 조회/수정/삭제 시 해당 ID의 그룹이 없거나 소프트 삭제(is_deleted='Y')된 경우
     * HTTP 상태: 404 Not Found
     */
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "EG001", "존재하지 않는 그룹입니다."),

    /**
     * 존재하지 않는 그룹 멤버입니다.
     *
     * 상황: 그룹 멤버 매핑(GroupMember)이 존재하지 않을 때
     * HTTP 상태: 404 Not Found
     */
    GROUP_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "EG002", "존재하지 않는 그룹 멤버입니다."),

    /**
     * 그룹장 권한이 필요합니다.
     *
     * 상황: 그룹장만 수행할 수 있는 작업(승인/거절/추방/수정/삭제 등)을 일반 멤버가 요청할 때
     * HTTP 상태: 403 Forbidden
     */
    NOT_GROUP_LEADER(HttpStatus.FORBIDDEN, "EG003", "그룹장만 수행할 수 있는 작업입니다."),

    /**
     * 그룹 멤버가 아닙니다.
     *
     * 상황: 그룹원 전용 기능(멤버 목록 보기 등)을 그룹에 가입하지 않은 사용자가 요청할 때
     * HTTP 상태: 403 Forbidden
     */
    NOT_GROUP_MEMBER(HttpStatus.FORBIDDEN, "EG004", "그룹 멤버가 아닙니다."),

    /**
     * 이미 가입한 그룹입니다.
     *
     * 상황: 이미 APPROVED 상태인 그룹에 다시 가입 신청/초대를 시도할 때
     * HTTP 상태: 409 Conflict
     */
    ALREADY_GROUP_MEMBER(HttpStatus.CONFLICT, "EG005", "이미 가입한 그룹입니다."),

    /**
     * 이미 가입 신청을 한 그룹입니다.
     *
     * 상황: PENDING 상태인 그룹에 중복으로 가입 신청을 시도할 때
     * HTTP 상태: 409 Conflict
     */
    ALREADY_GROUP_APPLIED(HttpStatus.CONFLICT, "EG006", "이미 가입 신청을 한 그룹입니다."),

    /**
     * 그룹 정원이 가득 찼습니다.
     *
     * 상황: 그룹 정원(max_members)을 초과하여 멤버를 승인/수락할 수 없을 때
     * HTTP 상태: 409 Conflict
     */
    GROUP_IS_FULL(HttpStatus.CONFLICT, "EG007", "그룹 정원이 가득 찼습니다."),

    /**
     * 그룹장은 그룹을 나갈 수 없습니다.
     *
     * 상황: 그룹장(LEADER)이 그룹 나가기/탈퇴를 시도할 때.
     * 다른 멤버에게 그룹장을 위임한 후 나갈 수 있습니다.
     * HTTP 상태: 400 Bad Request
     */
    GROUP_LEADER_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "EG008", "그룹장은 그룹을 나갈 수 없습니다. 다른 멤버에게 그룹장을 위임한 후 나가주세요."),

    /**
     * 존재하지 않는 초대입니다.
     *
     * 상황: 초대 응답 시 초대(INVITED 상태의 GroupMember)가 없거나, 초대받은 본인이 아닐 때 (정보 노출 방지)
     * HTTP 상태: 404 Not Found
     */
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "EG009", "존재하지 않는 초대입니다."),

    /**
     * 자기 자신을 초대할 수 없습니다.
     *
     * 상황: 그룹장이 본인을 그룹에 초대하려고 할 때
     * HTTP 상태: 400 Bad Request
     */
    CANNOT_INVITE_SELF(HttpStatus.BAD_REQUEST, "EG010", "자기 자신을 초대할 수 없습니다."),

    /**
     * 그룹장을 강등하거나 제거할 수 없습니다.
     *
     * 상황: 그룹장(LEADER)을 MEMBER로 강등하거나 추방하려고 할 때.
     * 그룹장 변경은 다른 멤버를 LEADER로 승격(위임)하는 방식으로만 가능합니다.
     * HTTP 상태: 400 Bad Request
     */
    GROUP_LEADER_CANNOT_DEMOTE(HttpStatus.BAD_REQUEST, "EG011", "그룹장을 강등하거나 제거할 수 없습니다. 다른 멤버에게 그룹장을 위임해주세요."),

    /**
     * 현재 가입 상태에서는 처리할 수 없는 요청입니다.
     *
     * 상황: 승인 대기(PENDING)가 아닌 신청을 승인/거절/취소하려 하거나,
     * 초대 대기(INVITED)가 아닌 상태에서 초대에 응답하려고 할 때
     * HTTP 상태: 400 Bad Request
     */
    INVALID_JOIN_STATUS(HttpStatus.BAD_REQUEST, "EG012", "현재 가입 상태에서는 처리할 수 없는 요청입니다."),

    // ------- EU: 사용자 (User) -------
    /**
     * 성별을 변경할 수 없습니다.
     *
     * 상황: 성별이 이미 설정된(N이 아닌) 사용자가 성별을 다른 값으로 변경하려고 할 때.
     * 성별은 N(선택 안 함) 상태에서만 M/F로 변경할 수 있으며, 한 번 설정하면 변경할 수 없습니다.
     * HTTP 상태: 400 Bad Request
     */
    GENDER_CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "EU001", "성별은 이미 설정되어 변경할 수 없습니다.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String code;           // 비즈니스 커스텀 코드
    private final String message;        // 사용자에게 보여줄 메시지
}