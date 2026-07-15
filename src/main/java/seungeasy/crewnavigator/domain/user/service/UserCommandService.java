package seungeasy.crewnavigator.domain.user.service;

import seungeasy.crewnavigator.domain.user.dto.request.UserInfoChangeRequest;

/**
 * <pre>
 *  Interface Name: UserCommandService
 *  Description: 사용자 정보 변경(쓰기) 비즈니스 로직을 정의하는 서비스 인터페이스.
 *
 * History
 * 2026.07.13: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface UserCommandService {

    /**
     * 사용자 정보를 변경합니다.
     *
     * @param userId  변경할 사용자 ID
     * @param request 변경할 정보
     */
    void changeMyInfo(String userId, UserInfoChangeRequest request);
}
