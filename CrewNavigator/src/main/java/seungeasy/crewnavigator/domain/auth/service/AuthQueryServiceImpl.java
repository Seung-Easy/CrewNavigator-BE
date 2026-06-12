package seungeasy.crewnavigator.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.dto.request.FindIdRequest;
import seungeasy.crewnavigator.domain.auth.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 *  Class Name: AuthQueryServiceImpl
 *  Description: 인증/계정 관련 읽기(Query) 작업을 처리하는 서비스 구현체.
 *
 *  [주요 기능]
 *  - 아이디 찾기
 *  - 내 정보 조회
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> findUserId(FindIdRequest request) {
        Optional<User> user = userRepository.findByNameAndEmail(request.name(), request.email());

        if (user.isEmpty()) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        return List.of(user.get().getUserId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        return new UserInfoResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getUserImage()
        );
    }
}
