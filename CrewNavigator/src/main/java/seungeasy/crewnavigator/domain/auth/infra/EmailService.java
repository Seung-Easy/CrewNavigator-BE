package seungeasy.crewnavigator.domain.auth.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import seungeasy.crewnavigator.common.infra.redis.RedisService;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *  Class Name: EmailService
 *  Description: 이메일 인증코드 생성, 발송, 검증을 처리하는 서비스.
 *  JavaMailSender를 통해 실제 이메일을 발송하며, 인증코드는 Redis에 5분간 저장됩니다.
 *
 * History
 * 2026.06.15: Seung-Geon: 클래스 생성
 * 2026.06.16: Seung-Geon: sendVerificationCode(), verifyCode() type 파라미터 추가 (용도별 Redis 키 분리)
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a 6-digit verification code, saves it to Redis with a 5-minute TTL,
     * and sends it to the specified email address. The type parameter distinguishes
     * the purpose (signup, findid, reset) so verification keys are isolated per flow.
     *
     * @param email the recipient's email address
     * @param type  the purpose of verification (signup, findid, reset)
     */
    public void sendVerificationCode(String email, String type) {
        String code = String.format("%06d", random.nextInt(1000000));
        log.debug("Generated verification code for {} (type={}): {}", email, type, code);

        redisService.save("email:code:" + type + ":" + email, code, 5, TimeUnit.MINUTES);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[CrewNavigator] 이메일 인증코드");
            message.setText("인증코드: " + code);
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send verification email to {} (type={})", email, type, e);
        }
    }

    /**
     * Verifies the provided code against the code stored in Redis for the given email and type.
     * If they match, the code is deleted from Redis.
     *
     * @param email the user's email address
     * @param code  the verification code to check
     * @param type  the purpose of verification (signup, findid, reset)
     * @return true if the code matches, false otherwise
     */
    public boolean verifyCode(String email, String code, String type) {
        String codeKey = "email:code:" + type + ":" + email;
        Object storedCode = redisService.get(codeKey);
        if (storedCode == null) {
            return false;
        }
        if (!storedCode.toString().equals(code)) {
            return false;
        }
        redisService.delete(codeKey);
        return true;
    }
}
