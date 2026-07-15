package seungeasy.crewnavigator.common.infra.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 *  Interface Name: S3Config
 *  Description: S3와 관련된 객체를 bean으로 등록
 *
 * History
 * 2026.07.14: Seung-Geon: 클래스 생성
 * 2026.07.14: Seung-Geon: S3관련 의존성 추가
 *                          해당 의존성인 `implementation 'io.awspring.cloud:spring-cloud-starter-aws:2.4.4'`은 자체적으로
 *                          yml파일을 읽어 AmazonS3 객체를 생성해 빈으로 등록하기 때문에 해당 파일이 필요 없어짐
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */

@Configuration
public class S3Config {

//    // value 뒤에 ':'를 붙여 yml 값이 없으면 빈 문자열("")이 들어오게 만듬
//    @Value("${AWS.S3.ACCESS_KEY_ID:}")
//    private String accessKey;
//
//    @Value("${AWS.S3.SECRET_ACCESS_KEY:}")
//    private String secretKey;
//
//    @Value("${AWS.S3.BUCKET:}")
//    private String bucket;
//
//    @Value("${AWS.S3.REGION:}")
//    private String region;
//
//    @Bean
//    public AmazonS3 amazonS3() {
//
//    }
}
