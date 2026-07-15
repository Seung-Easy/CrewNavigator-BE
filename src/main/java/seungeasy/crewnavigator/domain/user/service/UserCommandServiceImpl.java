package seungeasy.crewnavigator.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.user.dto.request.UserInfoChangeRequest;
import seungeasy.crewnavigator.domain.user.entity.UserInfo;
import seungeasy.crewnavigator.domain.user.repository.UserInfoRepository;

import java.util.Optional;

/**
 * <pre>
 *  Class Name: UserCommandServiceImpl
 *  Description: 사용자 정보 변경(쓰기) 비즈니스 로직 구현체.
 *  프로필 수정(이름, 생일, 주소, 전화번호, 이미지)을 처리합니다.
 *
 * History
 * 2026.07.13: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService{

    private final UserInfoRepository userInfoRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeMyInfo(String userId, UserInfoChangeRequest request) {

        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        log.info(request.toString());

        if (request.name() != null) {
            log.info("name 변경");
            userInfo.setName(request.name());
        }
        if (request.birthday() != null) {
            log.info("birthday 변경");
            userInfo.setBirthday(request.birthday());
        }
        if (request.address() != null) {
            log.info("address 변경");
            userInfo.setAddress(request.address());
        }
        if(request.phone() != null) {
            log.info("phone 변경");
            userInfo.setPhone(request.phone());
        }
        if(request.image() != null) {
            log.info("image 변경");
            userInfo.setUserImage(request.image());
        }
        log.info("변경 끝");
        userInfoRepository.save(userInfo);
    }
}
