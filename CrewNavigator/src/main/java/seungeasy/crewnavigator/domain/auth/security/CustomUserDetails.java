package seungeasy.crewnavigator.domain.auth.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import seungeasy.crewnavigator.domain.auth.entity.User;

import java.util.Collection;

/**
 * <pre>
 *  Class Name: CustomUserDetails
 *  Description: Spring Security의 UserDetails 구현체.
 *  User 엔티티를 래핑하여 SecurityContext에서 사용자 정보를 제공합니다.
 *
 *  [주요 정보]
 *  - 사용자 계정 상태 (잠금/활성)를 User 엔티티에 위임하여 동적으로 판단
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.12: Seung-Geon: getAuthorities의 반환값을 ROLE_USER 고정에서 유저의 권한으로 변경
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isAccountActive();
    }
}
