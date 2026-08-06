package seungeasy.crewnavigator.domain.auth.security.oauth2;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;

import java.io.IOException;

/**
 * <pre>
 *  Class Name: OAuth2AuthenticationFailureHandler
 *  Description: OAuth2 로그인 실패 시 JSON 에러 응답을 반환하는 핸들러.
 *               기존 form 로그인(AuthenticationFilter.unsuccessfulAuthentication)과
 *               동일한 에러 응답 포맷을 따릅니다.
 *
 * History
 * 2026.07.04: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.warn("OAuth2 login failed: {}", exception.getMessage());

        CustomResponse<Void> errorResponse = CustomResponse.error(ResponseCode.UNAUTHORIZED_ACCESS);

        response.setStatus(ResponseCode.UNAUTHORIZED_ACCESS.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
