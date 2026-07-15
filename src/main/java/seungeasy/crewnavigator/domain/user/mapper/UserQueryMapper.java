package seungeasy.crewnavigator.domain.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import seungeasy.crewnavigator.domain.user.dto.UserInfoRow;

/**
 * <pre>
 *  Interface Name: UserQueryMapper
 *  Description: 사용자 정보 조회(읽기) SQL 매핑 인터페이스.
 *  MyBatis XML 매퍼와 연동되어 SQL을 실행합니다.
 *
 * History
 * 2026.07.13: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Mapper
public interface UserQueryMapper {

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 행 (UserInfoRow)
     */
    UserInfoRow getUserInfo(@Param("userId") String userId);

}
