package seungeasy.crewnavigator.common.infra.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 *  Class Name: S3Config
 *  Description: S3와 관련된 객체를 bean으로 등록
 *
 *  [참고]
 *  spring-cloud-starter-aws의 자동 설정이 작동하지 않을 경우,
 *  수동으로 AmazonS3 빈을 생성합니다.
 *
 * History
 * 2026.07.14: Seung-Geon: 클래스 생성
 * 2026.07.16: Seung-Geon: 자동 설정 실패 대비 수동 빈 생성 구현
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */

@Configuration
public class S3Config {

    @Value("${AWS.S3.ACCESS_KEY_ID:}")
    private String accessKey;

    @Value("${AWS.S3.SECRET_ACCESS_KEY:}")
    private String secretKey;

    @Value("${AWS.S3.REGION:}")
    private String region;

    /**
     * AmazonS3 클라이언트를 생성합니다.
     *
     * @return AmazonS3 빈
     */
    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }
}
