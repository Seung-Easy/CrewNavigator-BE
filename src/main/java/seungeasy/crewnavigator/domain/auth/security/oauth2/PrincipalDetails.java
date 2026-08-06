package seungeasy.crewnavigator.domain.auth.security.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.security.CustomUserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  Class Name: PrincipalDetails
 *  Description: OAuth2를 위한 OAuth2User의 구현체.
 *  User 엔티티를 래핑하여 SecurityContext에서 사용자 정보를 제공합니다.
 *
 *  [주요 정보]
 *
 * History
 * 2026.06.29: Seung-Geon: 예제를 참고하여 작성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
public class PrincipalDetails extends CustomUserDetails implements OAuth2User {

    private Map<String, Object> attributes;

    // OAuth2 로그인 (권한 정보 포함)
    public PrincipalDetails(User user, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        super(user, authorities);
        this.attributes = attributes;
    }

    // OAuth2 로그인 (권한 정보 없음 — authorities를 빈 리스트로 초기화)
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        super(user, List.of());
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
