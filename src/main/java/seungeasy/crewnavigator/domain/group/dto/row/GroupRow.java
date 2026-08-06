package seungeasy.crewnavigator.domain.group.dto.row;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: GroupRow
 *  Description: MyBatis 매핑용 그룹 Row DTO.
 *  GroupResponse로 변환되기 전 MyBatis 조회 결과를 담는 중간 객체입니다.
 *  memberCount는 서브쿼리, tags는 GROUP_CONCAT 결과를 문자열로 받아 service에서 List로 변환합니다.
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 조회 MyBatis 전환(CQRS)을 위해 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
@Setter
public class GroupRow {

    /** 그룹 번호 (PK) */
    private Long groupId;

    /** 그룹장 회원 아이디 */
    private String leaderId;

    /** 그룹명 */
    private String groupName;

    /** 그룹 정원 */
    private Long maxMembers;

    /** 그룹 소개 */
    private String description;

    /** 비공개 여부 ("Y"/"N", "Y"면 그룹 검색에서 제외) */
    private String isPrivate;

    /** 삭제 여부 ("Y"/"N", 소프트 삭제) */
    private String isDeleted;

    /** 그룹 이미지 경로 */
    private String groupImage;

    /** 그룹 생성 일시 */
    private LocalDateTime createdAt;

    /** 그룹 수정 일시 */
    private LocalDateTime updatedAt;

    /** 현재 가입 멤버 수 (APPROVED 기준, 서브쿼리) */
    private Long memberCount;

    /** 태그 이름 목록 (GROUP_CONCAT 결과, service에서 분리) */
    private String tags;
}
