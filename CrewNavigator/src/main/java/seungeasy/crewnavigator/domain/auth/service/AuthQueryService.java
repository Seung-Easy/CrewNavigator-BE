package seungeasy.crewnavigator.domain.auth.service;

import seungeasy.crewnavigator.domain.auth.dto.request.FindIdRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.UserInfoResponse;

import java.util.List;

public interface AuthQueryService {

    List<String> findUserId(FindIdRequest request);

    UserInfoResponse getUserInfo(String userId);
}
