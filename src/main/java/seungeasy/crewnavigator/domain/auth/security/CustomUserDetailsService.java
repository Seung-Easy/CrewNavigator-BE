package seungeasy.crewnavigator.domain.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;
import seungeasy.crewnavigator.domain.auth.repository.UserRoleRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 *  Class Name: CustomUserDetailsService
 *  Description: Spring Security의 UserDetailsService 구현체.
 *  DB에서 사용자 정보를 조회하여 CustomUserDetails로 변환합니다.
 *
 *  [주요 기능]
 *  - 사용자 ID로 사용자 엔티티 조회
 *  - CustomUserDetails 객체 생성 및 반환
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.12: Seung-Geon: BusinessException → UsernameNotFoundException 변경, UserRoleRepository 주입 및 Role 조회 로직 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * 사용자 ID로 사용자 정보와 권한을 함께 조회하여 UserDetails 객체를 반환합니다.
     * 사용자가 존재하지 않으면 UsernameNotFoundException을 발생시킵니다.
     *
     * @param userId 조회할 사용자 ID
     * @return CustomUserDetails (User 엔티티 + 권한 목록)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        // 사용자의 실제 Role 목록을 DB에서 조회
        List<SimpleGrantedAuthority> authorities = userRoleRepository.findByIdUserId(userId)
                .stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
                .collect(Collectors.toList());

        return new CustomUserDetails(user, authorities);
    }
}
