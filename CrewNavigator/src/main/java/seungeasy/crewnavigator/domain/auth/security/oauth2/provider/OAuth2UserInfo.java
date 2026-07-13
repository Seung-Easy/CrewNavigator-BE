package seungeasy.crewnavigator.domain.auth.security.oauth2.provider;

/**
 * <pre>
 *  Class Name: OAuth2UserInfo
 *  Description: OAuth2를 통해 사용자 정보를 받아올 때 사용하는 인터페이스
 *              각 서비스마다 사용자 정보를 제공하는 데이터 형식이 모두 다르기 때문에 개별적으로 구현체를 만들어야 함
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
public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getProviderEmail();
    String getProviderName();
}
