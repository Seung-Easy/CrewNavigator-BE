package seungeasy.crewnavigator.domain.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.infra.s3.S3Service;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.user.dto.request.UserInfoChangeRequest;
import seungeasy.crewnavigator.domain.user.entity.UserInfo;
import seungeasy.crewnavigator.domain.user.repository.UserInfoRepository;
import seungeasy.crewnavigator.domain.user.type.GenderType;


/**
 * <pre>
 *  Class Name: UserCommandServiceImpl
 *  Description: 사용자 정보 변경(쓰기) 비즈니스 로직 구현체.
 *  프로필 수정(이름, 생일, 주소, 전화번호, 성별, 이미지)을 처리합니다.
 *  성별은 N(선택 안 함) 상태에서만 M/F로 변경할 수 있으며, 한 번 설정하면 변경할 수 없습니다.
 *
 * History
 * 2026.07.13: Seung-Geon: 클래스 생성
 * 2026.07.16: Seung-Geon: 프로필 이미지 등록, 삭제 추가, @Transactional 추가, PII 로깅 제거
 * 2026.08.13: Seung-Geon: 성별 변경 로직 추가 (N 상태에서만 변경 가능)
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
    private final S3Service s3Service;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void changeMyInfo(String userId, UserInfoChangeRequest request) {

        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        if (request.name() != null) {
            userInfo.setName(request.name());
        }
        if (request.birthday() != null) {
            userInfo.setBirthday(request.birthday());
        }
        if (request.address() != null) {
            userInfo.setAddress(request.address());
        }
        if(request.phone() != null) {
            userInfo.setPhone(request.phone());
        }
        if(request.image() != null) {
            userInfo.setUserImage(request.image());
        }
        if (request.gender() != null) {
            GenderType requestedGender = GenderType.valueOf(request.gender());

            // 성별은 N(선택 안 함) 상태에서만 M/F로 변경할 수 있으며, 한 번 설정하면 변경할 수 없다.
            if (userInfo.getGender() != GenderType.N
                    && userInfo.getGender() != requestedGender) {
                throw new BusinessException(ResponseCode.GENDER_CHANGE_NOT_ALLOWED);
            }
            userInfo.setGender(requestedGender);
        }

        userInfoRepository.save(userInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void uploadImage(String userId, MultipartFile file) {
        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        String directory = "user/profile/image";

        String s3Key = s3Service.uploadFile(file, directory);

        userInfo.setUserImage(s3Key);

        userInfoRepository.save(userInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteImage(String userId) {
        UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        s3Service.deleteFile(userInfo.getUserImage());

        userInfo.setUserImage(null);

        userInfoRepository.save(userInfo);
    }
}
