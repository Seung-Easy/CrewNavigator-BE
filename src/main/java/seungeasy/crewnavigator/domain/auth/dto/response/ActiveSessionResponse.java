package seungeasy.crewnavigator.domain.auth.dto.response;

/**
 * <pre>
 *  Class Name: ActiveSessionResponse
 *  Description: 현재 로그인 중인 활성 세션 응답 DTO.
 *  admin이 로그인 중인 회원을 조회할 때 사용됩니다.
 *
 *  @param userId      사용자 ID
 *  @param sessionCount 해당 사용자의 활성 세션 수
 *
 * History
 * 2026.06.22: Seung-Geon: 최초 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record ActiveSessionResponse(
        String userId,
        int sessionCount
) {}
