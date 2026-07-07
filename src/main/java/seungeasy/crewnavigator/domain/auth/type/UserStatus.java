package seungeasy.crewnavigator.domain.auth.type;

/**
 * <pre>
 *  Enum Name: UserStatus
 *  Description: 사용자 계정 상태를 나타내는 열거형.
 *
 *  - ACTIVE: 정상 계정
 *  - INACTIVE: 관리자에 의해 비활성화된 계정
 *  - LEAVE: 탈퇴한 계정 (soft delete)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    LEAVE
}
