package seungeasy.crewnavigator.domain.group.repository;

import seungeasy.crewnavigator.domain.group.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: TagRepository
 * Description: 태그(Tag)에 대한 데이터 접근 인터페이스.
 *
 * History
 * 2026.08.02: Seung-Geon: 그룹 도메인 확장에 맞춰 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 태그 이름으로 조회합니다. (tag_name UNIQUE)
     *
     * @param tagName 태그 이름
     * @return 해당 태그 (없으면 empty)
     */
    Optional<Tag> findByTagName(String tagName);

    /**
     * 태그 이름 목록으로 일괄 조회합니다.
     *
     * @param tagNames 조회할 태그 이름 목록
     * @return 일치하는 태그 목록
     */
    List<Tag> findByTagNameIn(Collection<String> tagNames);
}
