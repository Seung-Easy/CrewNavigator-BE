package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: InvitationRow
 *  Description: MyBatis 매핑용 그룹 초대(INVITED) Row DTO.
 *  내가 초대받은 그룹 목록 조회 결과를 담는 중간 객체입니다.
 *  GroupRow의 그룹 정보에 초대 매핑 정보(invitationId, invitedAt)가 추가된 형태입니다.
 *
 * History
 * 2026.09.02: Seung-Geon: 내가 초대받은 그룹 목록 조회 기능을 위해 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@Setter
public class InvitationRow {

    /** 그룹 멤버 매핑 번호 (초대 수락/거절 API에 사용) */
    private Long invitationId;

    /** 그룹 번호 (PK) */
    private Long groupId;

    /** 그룹장 회원 아이디 */
    private String leaderId;

    /** 그룹장 이름 */
    private String leaderName;

    /** 그룹명 */
    private String groupName;

    /** 그룹 정원 */
    private Long maxMembers;

    /** 그룹 소개 */
    private String description;

    /** 비공개 여부 ("Y"/"N") */
    private String isPrivate;

    /** 그룹 이미지 경로 */
    private String groupImage;

    /** 현재 가입 멤버 수 (APPROVED 기준, 서브쿼리) */
    private Long memberCount;

    /** 태그 이름 목록 (GROUP_CONCAT 결과, service에서 분리) */
    private String tags;

    /** 초대 일시 */
    private LocalDateTime invitedAt;
}
