package seungeasy.crewnavigator.domain.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import seungeasy.crewnavigator.domain.auth.dto.row.AdminUserRow;
import seungeasy.crewnavigator.domain.auth.dto.row.RoleCountRow;
import seungeasy.crewnavigator.domain.auth.dto.row.StatusCountRow;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 *  Interface Name: AuthQueryMapper
 *  Description: 인증/계정 관련 읽기(Query) 작업을 위한 MyBatis Mapper.
 *  AuthQueryServiceImpl에서 JPA 대신 사용됩니다. (CQRS: Query는 MyBatis)
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성 (JPA @Query → MyBatis 마이그레이션)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Mapper
public interface AuthQueryMapper {

    /** 이름과 이메일로 가입된 사용자 ID를 조회합니다. */
    String findUserId(@Param("name") String name, @Param("email") String email);

    // ========== 회원 목록 검색 (페이징) ==========

    /** 검색 조건에 맞는 전체 회원 수를 조회합니다. */
    long countUsers(@Param("status") String status, @Param("keyword") String keyword);

    /** 검색 조건에 맞는 회원 목록을 페이징 조회합니다. */
    List<AdminUserRow> searchUsers(@Param("status") String status, @Param("keyword") String keyword,
                                   @Param("offset") int offset, @Param("size") int size);

    // ========== 관리자 회원 상세 ==========

    /** 특정 사용자의 상세 정보(권한 포함)를 조회합니다. */
    AdminUserRow getAdminUserDetail(@Param("userId") String userId);

    // ========== 통계 ==========

    /** 전체 회원 수를 조회합니다. */
    long countTotalUsers();

    /** 상태별 회원 수를 조회합니다. */
    List<StatusCountRow> countUsersByStatus();

    /** 오늘 가입한 회원 수를 조회합니다. */
    long countTodaySignups(@Param("startOfDay") LocalDateTime startOfDay);

    /** 전체 가입자 수 (탈퇴 포함)를 조회합니다. */
    long countTotalSignups();

    /** 권한별 회원 수를 조회합니다. */
    List<RoleCountRow> countUsersByRole();

    // ========== Role ==========

    /** 전체 Role명 목록을 조회합니다. */
    List<String> getAllRoleNames();
}
