package seungeasy.crewnavigator.domain.auth.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.auth.repository.LoginHistoryRepository;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: LoginHistoryCleanupScheduler
 *  Description: 일정 기간이 지난 로그인 이력을 정리하는 스케줄러.
 *  
 *  - 90일이 초과된 로그인 이력 행을 완전 삭제합니다.
 *  - 매일 새벽 3시에 실행됩니다.
 *  - IP 주소만 삭제하는 것이 아니라 행 자체를 삭제합니다.
 *    90일이 지난 로그는 감사 가치가 낮고, IP가 없는 불완전한 데이터는 의미가 없기 때문입니다.
 *
 * History
 * 2026.06.12: Seung-Geon: 최초 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginHistoryCleanupScheduler {

    private final LoginHistoryRepository loginHistoryRepository;

    /**
     * 매일 새벽 3시에 실행되어 90일 초과 로그인 이력을 삭제합니다.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldLoginHistory() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(90);
        log.info("로그인 이력 정리 스케줄러 실행: {} 이전 데이터 삭제", cutoff);

        try {
            loginHistoryRepository.deleteByLoginAtBefore(cutoff);
            log.info("로그인 이력 정리 완료");
        } catch (Exception e) {
            log.error("로그인 이력 정리 중 오류 발생", e);
        }
    }
}
