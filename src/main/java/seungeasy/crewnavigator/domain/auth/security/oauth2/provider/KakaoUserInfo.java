package seungeasy.crewnavigator.domain.auth.security.oauth2.provider;

import java.util.Map;

/**
 * <pre>
 *  Class Name: KakaoUserInfo
 *  Description: Kakao를 사용하는 경우 사용할 인터페이스 구현체
 *              사용자 ID 필드명: id
 *              사용자 이름 필드명: kakao_account.profile.nickname
 *  [주요 정보]
 *
 * History
 * 2026.06.29: Seung-Geon: 예제를 참고하여 작성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> kakaoProfile;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderEmail() {
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getProviderName() {
        return (String) kakaoProfile.get("nickname");
    }
}
