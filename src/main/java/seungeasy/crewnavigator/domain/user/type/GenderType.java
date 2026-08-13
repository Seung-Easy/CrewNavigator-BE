package seungeasy.crewnavigator.domain.user.type;

/**
 * <pre>
 *  Enum Name: GenderType
 *  Description: 사용자 성별을 나타내는 열거형.
 *
 *  - M: 남성 (Male)
 *  - F: 여성 (Female)
 *  - N: 선택 안 함 (None) - OAuth2 가입 등 성별 정보가 없을 때의 기본값.
 *      성별은 N 상태에서만 M/F로 변경할 수 있으며, 한 번 설정하면 변경할 수 없습니다.
 *
 *  History
 * 2026.08.13: Seung-Geon: 성별 속성 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public enum GenderType {
    M, F, N
}
