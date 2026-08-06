package seungeasy.crewnavigator.domain.group.type;

/**
 * <pre>
 *  Enum Name: GroupMemberRole
 *  Description: 그룹 내 멤버 권한을 나타내는 열거형.
 *
 *  - LEADER: 그룹장 (그룹 생성자가 가지며, 멤버 승인/거절 권한 보유)
 *  - MEMBER: 일반 멤버
 *
 * History
 * 2026.07.31: Seung-Geon: 그룹 도메인 확장을 위한 enum 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public enum GroupMemberRole {
    LEADER,
    MEMBER
}
