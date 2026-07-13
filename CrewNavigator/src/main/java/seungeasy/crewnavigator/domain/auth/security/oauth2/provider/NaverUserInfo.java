package seungeasy.crewnavigator.domain.auth.security.oauth2.provider;

import java.util.Map;

/**
 * <pre>
 *  Class Name: NaverUserInfo
 *  Description: Naver를 사용하는 경우 사용할 인터페이스 구현체
 *              naver의 경우 모든 정보를 response라는 객체 안에 담아서 전송
 *              사용자 ID 필드명: response.id
 *              사용자 이름 필드명: response.name
 *  [주요 정보]
 *
 * History
 * 2026.06.29: Seung-Geon: 예제를 참고하여 작성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public class NaverUserInfo implements OAuth2UserInfo{
    private final Map<String, Object> attributes; // naver는 response 키 값 안에 attributes가 들어있음

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProviderName() {
        String name = (String) attributes.get("name");
        return name != null ? name : (String) attributes.get("nickname");
    }
}
