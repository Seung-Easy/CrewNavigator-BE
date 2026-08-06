package seungeasy.crewnavigator.domain.auth.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.auth.entity.Role;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.entity.UserRole;
import seungeasy.crewnavigator.domain.auth.repository.RoleRepository;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;
import seungeasy.crewnavigator.domain.auth.repository.UserRoleRepository;
import seungeasy.crewnavigator.domain.auth.security.oauth2.provider.GoogleUserInfo;
import seungeasy.crewnavigator.domain.auth.security.oauth2.provider.KakaoUserInfo;
import seungeasy.crewnavigator.domain.auth.security.oauth2.provider.NaverUserInfo;
import seungeasy.crewnavigator.domain.auth.security.oauth2.provider.OAuth2UserInfo;

import java.util.UUID;

/**
 * <pre>
 *  Class Name: PrincipalOAuth2UserService
 *  Description: OAuth2 로그인 성공 후 제공자(Google/Kakao/Naver)로부터 사용자 정보를 가져와
 *              자동 회원가입 또는 기존 회원 조회를 처리하는 서비스.
 *
 *  [처리 흐름]
 *  1. 제공자로부터 사용자 정보를 받아옴 (loadUser)
 *  2. 제공자별로 다른 데이터 형식을 OAuth2UserInfo 구현체로 추상화
 *  3. provider_providerId 조합으로 기존 회원 조회
 *  4. 없으면 자동 회원가입 (provider, providerId 저장, ROLE_USER 부여)
 *  5. PrincipalDetails 반환
 *
 * History
 * 2026.06.29: Seung-Geon: 예제를 참고하여 작성
 * 2026.07.04: Seung-Geon: AI(oh-my-opencode) 버그 수정 및 리팩토링
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 제공자별 사용자 정보 추출
        OAuth2UserInfo oAuth2UserInfo = extractUserInfo(userRequest, oAuth2User);
        if (oAuth2UserInfo == null) {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: "
                    + userRequest.getClientRegistration().getRegistrationId());
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String loginId = provider + "_" + providerId;
        String email = oAuth2UserInfo.getProviderEmail();
        String name = oAuth2UserInfo.getProviderName();

        // 기존 회원 조회 또는 자동 회원가입
        User user = userRepository.findById(loginId).orElse(null);
        if (user == null) {
            user = createOAuth2User(loginId, email, name, provider);
            log.info("OAuth2 auto-signup: {} ({})", loginId, provider);
        } else {
            log.info("OAuth2 existing user login: {} ({})", loginId, provider);
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    /**
     * 제공자(registrationId)에 따라 적절한 OAuth2UserInfo 구현체로 사용자 정보를 추출합니다.
     *
     * @param userRequest OAuth2UserRequest
     * @param oAuth2User  OAuth2User (제공자로부터 받은 raw 데이터)
     * @return OAuth2UserInfo 구현체, 지원하지 않는 제공자면 null
     */
    private OAuth2UserInfo extractUserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("google".equals(registrationId)) {
            return new GoogleUserInfo(oAuth2User.getAttributes());
        } else if ("kakao".equals(registrationId)) {
            return new KakaoUserInfo(oAuth2User.getAttributes());
        } else if ("naver".equals(registrationId)) {
            return new NaverUserInfo(oAuth2User.getAttributes());
        }

        return null;
    }

    /**
     * OAuth2 자동 회원가입을 수행합니다.
     * userId = provider_providerId 조합으로 생성하고, ROLE_USER를 부여합니다.
     *
     * @param loginId    userId (provider_providerId 형식)
     * @param email      이메일
     * @param name       이름
     * @param provider   제공자 (google/kakao/naver)
     * @return 생성된 User 엔티티
     */
    private User createOAuth2User(String loginId, String email, String name,
                                  String provider) {
        User user = new User();
        user.setUserId(loginId);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // OAuth2 사용자는 별도 비밀번호 없음
        user.setName(name != null ? name : provider + "_member");
        user.setEmail(email != null ? email : loginId + "@" + provider + ".oauth2");
        user.setPhone("000-0000-0000"); // OAuth2 사용자 전화번호는 미제공시 placeholder

        userRepository.save(user);

        // ROLE_USER 자동 부여
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER가 존재하지 않습니다. role 테이블을 확인해주세요."));
        UserRole userRoleEntity = new UserRole(userRole.getRoleId(), loginId);
        userRoleRepository.save(userRoleEntity);

        return user;
    }
}
