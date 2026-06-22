package seungeasy.crewnavigator.domain.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * <pre>
 *  Class Name: JwtProvider
 *  Description: JWT(Json Web Token) 생성, 검증, 파싱을 담당하는 컴포넌트.
 *
 *  [주요 기능]
 *  - Access Token / Refresh Token 생성
 *  - 토큰 서명 검증 및 유효성 확인
 *  - 토큰에서 사용자 ID 추출
 *  - 토큰 상태 상세 확인 (VALID / EXPIRED / INVALID)
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * 2026.06.12: Seung-Geon: TokenStatus enum 추가, getTokenStatus() 메서드 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:3600000}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:1209600000}")
    private long refreshTokenExpiration;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token을 생성합니다.
     *
     * @param userId 토큰의 subject로 저장할 사용자 ID
     * @return 생성된 JWT Access Token 문자열
     */
    public String generateAccessToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Refresh Token을 생성합니다. (Access Token보다 긴 만료 기간)
     *
     * @param userId 토큰의 subject로 저장할 사용자 ID
     * @return 생성된 JWT Refresh Token 문자열
     */
    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID(subject)를 추출합니다.
     * 토큰이 만료되었거나 서명이 유효하지 않으면 BusinessException을 발생시킵니다.
     *
     * @param token 파싱할 JWT 토큰
     * @return 토큰에 저장된 사용자 ID
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 토큰 만료 또는 서명 불일치 시
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ResponseCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ResponseCode.INVALID_TOKEN);
        }
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 만료되었거나 위조되었으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 상태를 상세 확인합니다. validateToken()과 달리 만료와 위조를 구분하여 반환합니다.
     *
     * @param token 확인할 JWT 토큰
     * @return TokenStatus.VALID / EXPIRED / INVALID
     */
    public TokenStatus getTokenStatus(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.INVALID;
        }
    }

    public enum TokenStatus {
        VALID,
        EXPIRED,
        INVALID
    }

    /**
     * JWT 토큰에서 만료 시간(expiration)을 추출합니다.
     *
     * @param token 파싱할 JWT 토큰
     * @return 토큰의 만료 시간
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 토큰 파싱 실패 시
     */
    public Date getExpirationFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration();
        } catch (JwtException e) {
            throw new BusinessException(ResponseCode.INVALID_TOKEN);
        }
    }

    /**
     * Access Token의 만료 시간(ms)을 반환합니다.
     *
     * @return Access Token 만료 시간 (밀리초)
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Refresh Token의 만료 시간(ms)을 반환합니다.
     *
     * @return Refresh Token 만료 시간 (밀리초)
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
