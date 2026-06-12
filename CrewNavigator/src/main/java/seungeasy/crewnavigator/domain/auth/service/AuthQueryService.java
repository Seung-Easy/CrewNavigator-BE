package seungeasy.crewnavigator.domain.auth.service;

import seungeasy.crewnavigator.domain.auth.dto.request.FindIdRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.UserInfoResponse;

import java.util.List;

/**
 * <pre>
 *  Interface Name: AuthQueryService
 *  Description: 인증/계정 관련 읽기(Query) 작업을 정의한 서비스 인터페이스.
 *
 *  [제공 기능]
 *  - 아이디 찾기
 *  - 내 정보 조회
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 인터페이스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
public interface AuthQueryService {

    /**
     * 이름과 이메일로 가입된 아이디 목록을 조회합니다.
     *
     * @param request 이름과 이메일 정보
     * @return 조회된 아이디 목록
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 일치하는 사용자가 없을 시
     */
    List<String> findUserId(FindIdRequest request);

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (UserInfoResponse)
     * @throws seungeasy.crewnavigator.common.exception.BusinessException 사용자를 찾을 수 없을 시
     */
    UserInfoResponse getUserInfo(String userId);
}
