package seungeasy.crewnavigator.common.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * <pre>
 * Class Name: S3Service
 * Description: AWS S3 파일 업로드 서비스
 *
 * History
 * 2026.07.15: Seung-Geon: 이전 프로젝트를 참고해 작성
 *                          예외처리에 넣을 ResponseCode를 추가하고, 그에 맞게 변경 필요
 * 2026.07.16: Seung-Geon: validateImageFile - 이미지 파일(jpg/jpeg/png/gif/webp)만 허용하도록 변경
 * </pre>
 *
 * @author 혜원
 * @version 1.1
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${AWS.S3.BUCKET:}")
    private String bucket;

    /**
     * 파일 업로드
     *
     * @param file 업로드할 파일
     * @param directory S3 디렉토리 경로
     * @return 업로드된 파일의 URL
     */
    public String uploadFile(MultipartFile file, String directory) {
        if(file.isEmpty()) {
            // 예외 코드 추가 필요
            // 지금은 임시로 아무거나 넣은 상태
            throw new BusinessException(ResponseCode.INVALID_VERIFICATION_CODE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            // 예외 코드 추가 필요
            // 지금은 임시로 아무거나 넣은 상태
            throw  new BusinessException(ResponseCode.INVALID_VERIFICATION_CODE);
        }

        // 확장자 확인
        String extension = getFileExtension(originalFilename);
        validateImageFile(extension);

        // 고유 파일명 생성
        String uniqueFilename = generateUniqueFilename(originalFilename);
        String s3Key = directory + "/" + uniqueFilename;

        try {
            // 메타 데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3 업로드
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket,
                    s3Key,
                    file.getInputStream(),
                    metadata
            ).withCannedAcl(CannedAccessControlList.Private);

            amazonS3.putObject(putObjectRequest);

            log.info("S3 파일 업로드 성공 - Key: {}", s3Key);

            return s3Key;
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패 - 파일: {}", originalFilename, e);

            // 예외 코드 추가 필요
            // 지금은 임시로 아무거나 넣은 상태
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 파일 삭제
     *
     * @param s3Key 삭제할 파일의 S3 키
     */
    public void deleteFile(String s3Key) {
        if (s3Key == null || s3Key.isEmpty()) {
            return;
        }

        try {
            if(amazonS3.doesObjectExist(bucket, s3Key)) {
                amazonS3.deleteObject(bucket, s3Key);
                log.info("S3 파일 삭제 성공 - Key: {}", s3Key);
            }
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패 - Key: {}", s3Key, e);
            // 삭제 실패해도 예외 던지기 X
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            throw new BusinessException(ResponseCode.INVALID_VERIFICATION_CODE);
        }

        return filename.substring(lastIndexOfDot + 1).toLowerCase();
    }

    /**
     * 파일 검증 (이미지 파일만 허용)
     */
    private void validateImageFile(String extension) {
        if (!extension.matches("jpg|jpeg|png|gif|webp")) {
            throw new BusinessException(ResponseCode.INVALID_VERIFICATION_CODE);
        }
    }

    /**
     * 고유 파일명 생성
     */
    private String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }

    /**
     * Presigned URL 생성
     *
     * @param s3Key S3 키
     * @param expirationDays 만료 일수
     * @return Presigned URL
     */
    public String generatePresignedUrl(String s3Key, int expirationDays) {
        if (s3Key == null || s3Key.isEmpty()) {
            return null;
        }

        try {
            //만료 시간 설정
            Date expiration = new Date();
            long expirationTime = expiration.getTime();
            expirationTime += (long) expirationDays * 24 * 60 * 60 * 1000;
            expiration.setTime(expirationTime);

            // Presigned URL 생성
            String presignedUrl = amazonS3.generatePresignedUrl(bucket, s3Key, expiration).toString();
            log.debug("Presigned URL 생성 - Key: {}, 만료: {}일", s3Key, expirationDays);

            return presignedUrl;
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패 - Key: {}", s3Key, e);
            throw new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Presigned URL 생성 (기본 7일)
     *
     * @param s3Key S3 키
     * @return Presigned URL
     */
    public String generatePresignedUrl(String s3Key) {
        return generatePresignedUrl(s3Key, 7);
    }
}
