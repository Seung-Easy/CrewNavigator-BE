package seungeasy.crewnavigator.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.infra.s3.S3Service;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.user.dto.UserInfoRow;
import seungeasy.crewnavigator.domain.user.dto.response.UserInfoResponse;
import seungeasy.crewnavigator.domain.user.mapper.UserQueryMapper;

/**
 * <pre>
 *  Class Name: UserQueryServiceImpl
 *  Description: 사용자 정보 조회(읽기) 비즈니스 로직 구현체.
 *  MyBatis Mapper를 통해 DB에서 사용자 정보를 조회합니다.
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
public class UserQueryServiceImpl implements UserQueryService{

    private final UserQueryMapper userQueryMapper;
    private final S3Service s3Service;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String userId) {
        UserInfoRow row = userQueryMapper.getUserInfo(userId);
        if (row == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        return new UserInfoResponse(
                row.userId(),
                row.name(),
                row.email(),
                row.phone(),
                row.gender(),
                s3Service.generatePresignedUrl(row.userImage()),
                row.createdAt()
        );
    }
}
