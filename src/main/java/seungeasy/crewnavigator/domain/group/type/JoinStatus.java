package seungeasy.crewnavigator.domain.group.type;

/**
 * <pre>
 *  Enum Name: JoinStatus
 *  Description: 그룹 가입/초대 상태를 나타내는 열거형.
 *
 *  - PENDING: 가입 신청 대기 (그룹장의 승인 대기)
 *  - APPROVED: 가입 승인 완료
 *  - REJECTED: 가입 거절
 *  - INVITED: 그룹장의 초대를 받은 상태 (수락 대기)
 *
 * History
 * 2026.07.31: Seung-Geon: 그룹 도메인 확장을 위한 enum 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public enum JoinStatus {
    PENDING,
    APPROVED,
    REJECTED,
    INVITED
}
