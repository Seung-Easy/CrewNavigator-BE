package seungeasy.crewnavigator.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seungeasy.crewnavigator.domain.group.entity.CrewGroup;

import java.util.List;

/**
 * <pre>
 * Interface Name: GroupRepository
 * Description: 그룹(CrewGroup)에 대한 데이터 접근 인터페이스.
 *
 * History
 * 2026.08.02: Seung-Geon: 비공개 그룹(isPrivate="Y") 검색 제외 조건 반영
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
public interface GroupRepository extends JpaRepository<CrewGroup, Long> {

    /**
     * 그룹명에 키워드를 포함하고, 공개(isPrivate="N")이며 삭제되지 않은 그룹을 조회합니다.
     * <p>
     * is_private는 그룹 검색 노출 여부를 나타내는 설정입니다. 비공개 그룹(isPrivate="Y")은
     * 검색 결과에서 제외됩니다.
     *
     * @param groupName  검색 키워드
     * @param isPrivate  공개 여부 ("N" = 공개, "Y" = 비공개)
     * @param isDeleted  삭제 여부 ("N" = 삭제되지 않은 그룹)
     * @return 검색된 그룹 목록
     */
    List<CrewGroup> findByGroupNameContainingAndIsPrivateAndIsDeleted(String groupName, String isPrivate, String isDeleted);
}
