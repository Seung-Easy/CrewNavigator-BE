package seungeasy.crewnavigator.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.group.entity.GroupWarning;

import java.util.List;

/**
 * <pre>
 * Interface Name: GroupWarningRepository
 * Description: 그룹 경고(GroupWarning)에 대한 데이터 접근 인터페이스.
 *
 * History
 * 2026.08.29: Seung-Geon: 그룹 경고 기능 구현을 위한 리포지토리 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface GroupWarningRepository extends JpaRepository<GroupWarning, Long> {

    /**
     * 특정 그룹의 경고 목록을 생성일시 내림차순으로 조회합니다.
     *
     * @param groupId 그룹 번호
     * @return 그룹 경고 목록
     */
    List<GroupWarning> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    /**
     * 특정 그룹의 모든 경고 기록을 삭제합니다.
     *
     * @param groupId 그룹 번호
     */
    void deleteByGroupId(Long groupId);
}
