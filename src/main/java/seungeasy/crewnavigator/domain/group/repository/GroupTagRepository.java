package seungeasy.crewnavigator.domain.group.repository;

import seungeasy.crewnavigator.domain.group.entity.GroupTag;
import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.group.entity.id.GroupTagId;

import java.util.List;

/**
 * <pre>
 * Interface Name: GroupTagRepository
 * Description: 그룹-태그 매핑(GroupTag)에 대한 데이터 접근 인터페이스.
 * 복합 키(tag_id, group_id)를 @EmbeddedId로 사용합니다.
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface GroupTagRepository extends JpaRepository<GroupTag, GroupTagId> {

    /**
     * 그룹에 속한 태그 매핑 목록을 조회합니다.
     *
     * @param groupId 그룹 번호
     * @return 그룹의 태그 매핑 목록
     */
    List<GroupTag> findByGroupTagId_GroupId(Long groupId);

    /**
     * 그룹의 모든 태그 매핑을 삭제합니다. (그룹 수정 시 태그 교체, 그룹 해산 시 사용)
     *
     * @param groupId 그룹 번호
     */
    void deleteByGroupTagId_GroupId(Long groupId);
}
