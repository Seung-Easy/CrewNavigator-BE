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
 *  - LEFT: 퇴장한 상태 (나가기/추방) — 나간 사유(leave_reason)와 날짜(left_at)가 기록되며,
 *          재신청 시 PENDING으로 재전환됩니다. (UK_group_user 제약 때문에 행을 보존)
 *
 * History
 * 2026.07.31: Seung-Geon: 그룹 도메인 확장을 위한 enum 생성
 * 2026.08.13: Seung-Geon: 나가기/추방 기록을 위한 LEFT 상태 추가 (soft-delete 전환)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public enum JoinStatus {
    PENDING,
    APPROVED,
    REJECTED,
    INVITED,
    LEFT
}
