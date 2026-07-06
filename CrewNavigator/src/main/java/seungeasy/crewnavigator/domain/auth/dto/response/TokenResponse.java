package seungeasy.crewnavigator.domain.auth.dto.response;

/**
 * <pre>
 *  Class Name: TokenResponse
 *  Description: JWT 토큰 발급/갱신 응답 DTO.
 *  Access Token 정보를 클라이언트에 반환합니다. (Refresh Token은 HttpOnly Cookie로 별도 전송)
 *
 *  @param accessToken  새로 발급된 Access Token
 *  @param refreshToken Refresh Token (로그인 응답에만 포함, 갱신 시에는 null)
 *  @param tokenType    토큰 타입 (고정: "Bearer")
 *  @param expiresIn    Access Token 만료까지 남은 시간 (초)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
