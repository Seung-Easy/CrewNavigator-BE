package seungeasy.crewnavigator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <pre>
 *  Class Name: CrewNavigatorApplication
 *  Description: CrewNavigator 프로젝트의 메인 애플리케이션 클래스.
 *  Spring Boot 기동 시 최초로 실행되는 진입점입니다.
 *
 * History
 * 2026.06.10: Seung-Geon: AI(oh-my-opencode)를 통한 클래스 생성
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class CrewNavigatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrewNavigatorApplication.class, args);
    }

}
