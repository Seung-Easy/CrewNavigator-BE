package seungeasy.crewnavigator.domain.auth.dto.response;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: LoginHistoryResponse
 *  Description: 로그인 이력 조회 응답 DTO.
 *  GET /auth/me/login-history 시 반환되는 로그인 이력 정보입니다.
 *
 *  @param seq          순번 (전체 기준 1=가장 오래된 로그인, 클수록 최신)
 *  @param loginHistoryId 로그인 이력 ID (DB PK)
 *  @param loginAt      로그인 일시
 *  @param ipAddress    접속 IP 주소
 *  @param isActivated  로그인 성공 여부 (true=성공, false=실패)
 *
 * History
 * 2026.06.22: Seung-Geon: 최초 생성
 * 2026.06.22: Seung-Geon: seq 필드 추가 (사용자 기준 순번)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public record LoginHistoryResponse(
        int seq,
        Long loginHistoryId,
        LocalDateTime loginAt,
        String ipAddress,
        Boolean isActivated
) {}
