package seungeasy.crewnavigator.domain.auth.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.repository.LoginHistoryRepository;
import seungeasy.crewnavigator.domain.auth.repository.PasswordHistoryRepository;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;
import seungeasy.crewnavigator.domain.auth.type.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 *  Class Name: UserCleanupScheduler
 *  Description: 일정 기간이 경과한 탈퇴(LEAVE) 계정을 완전 삭제(Hard Delete)하는 스케줄러.
 *
 *  - 탈퇴 후 1년이 초과된 계정을 DB에서 영구 제거합니다.
 *  - 연관된 로그인 이력, 비밀번호 변경 이력도 함께 삭제합니다.
 *  - 매일 새벽 3시 30분에 실행됩니다 (LoginHistoryCleanupScheduler와 시간 분산).
 *
 * History
 * 2026.06.16: Seung-Geon: 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;

    /**
     * 매일 새벽 3시 30분에 실행되어 탈퇴 후 1년 초과된 계정을 완전 삭제합니다.
     * 삭제 전 관련 이력(로그인 이력, 비밀번호 변경 이력)을 먼저 정리합니다.
     */
    @Scheduled(cron = "0 30 3 * * ?")
    @Transactional
    public void cleanupDeletedUsers() {
        LocalDateTime cutoff = LocalDateTime.now().minusYears(1);
        log.info("탈퇴 계정 정리 스케줄러 실행: {} 이전 탈퇴 계정 삭제", cutoff);

        try {
            List<User> expiredLeaves = userRepository.findAllByStatusAndDeletedAtBefore(
                    UserStatus.LEAVE, cutoff);

            if (expiredLeaves.isEmpty()) {
                log.info("정리할 탈퇴 계정이 없습니다.");
                return;
            }

            log.info("{}개의 탈퇴 계정을 정리합니다.", expiredLeaves.size());

            for (User user : expiredLeaves) {
                String userId = user.getUserId();

                // 연관 이력 먼저 삭제
                loginHistoryRepository.deleteByUserId(userId);
                passwordHistoryRepository.deleteByUserId(userId);
                log.debug("User {}: 연관 이력 삭제 완료", userId);
            }

            // 사용자 레코드 일괄 삭제
            userRepository.deleteAll(expiredLeaves);
            log.info("탈퇴 계정 정리 완료: {}개 계정 영구 삭제", expiredLeaves.size());

        } catch (Exception e) {
            log.error("탈퇴 계정 정리 중 오류 발생", e);
        }
    }
}
