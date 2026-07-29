package seungeasy.crewnavigator.domain.auth.security.oauth2.provider;

import java.util.Map;

/**
 * <pre>
 *  Class Name: GoogleUserInfo
 *  Description: Google을 사용하는 경우 사용할 인터페이스 구현체
 *              사용자 ID의 필드명: sub
 *              이름의 필드명: name
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
public class GoogleUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProviderName() {
        return (String) attributes.get("name");
    }
}
