package seungeasy.crewnavigator.domain.user.service;

import seungeasy.crewnavigator.domain.user.dto.response.UserInfoResponse;

public interface UserQueryService {

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (UserInfoResponse)
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 사용자를 찾을 수 없을 시
     */
    UserInfoResponse getUserInfo(String userId);
}
