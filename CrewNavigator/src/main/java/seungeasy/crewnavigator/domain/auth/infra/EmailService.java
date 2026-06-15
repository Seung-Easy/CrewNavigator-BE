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
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
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
     * and sends it to the specified email address.
     *
     * @param email the recipient's email address
     */
    public void sendVerificationCode(String email) {
        String code = String.format("%06d", random.nextInt(1000000));
        log.debug("Generated verification code for {}: {}", email, code);

        redisService.save("email:code:" + email, code, 5, TimeUnit.MINUTES);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[CrewNavigator] 이메일 인증코드");
            message.setText("인증코드: " + code);
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send verification email to {}", email, e);
        }
    }

    /**
     * Verifies the provided code against the code stored in Redis for the given email.
     * If they match, the code is deleted from Redis.
     *
     * @param email the user's email address
     * @param code the verification code to check
     * @return true if the code matches, false otherwise
     */
    public boolean verifyCode(String email, String code) {
        Object storedCode = redisService.get("email:code:" + email);
        if (storedCode == null) {
            return false;
        }
        if (!storedCode.toString().equals(code)) {
            return false;
        }
        redisService.delete("email:code:" + email);
        return true;
    }
}
