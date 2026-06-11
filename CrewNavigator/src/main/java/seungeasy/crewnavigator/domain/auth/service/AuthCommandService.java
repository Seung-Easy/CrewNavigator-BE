package seungeasy.crewnavigator.domain.auth.service;

import seungeasy.crewnavigator.domain.auth.dto.request.*;
import seungeasy.crewnavigator.domain.auth.dto.response.TokenResponse;

public interface AuthCommandService {

    void signup(SignupRequest request);

    TokenResponse refreshToken(String refreshToken);

    void logout(String accessToken, String userId);

    void changePassword(String userId, PasswordChangeRequest request);

    void resetPassword(PasswordResetRequest request);

    void deleteAccount(String userId);

    void forceLogout(String userId, String adminId);
}
