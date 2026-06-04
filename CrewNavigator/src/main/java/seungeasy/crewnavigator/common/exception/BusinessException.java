package seungeasy.crewnavigator.common.exception;

import lombok.Getter;
import seungeasy.crewnavigator.common.response.ResponseCode;

/**
 * <pre>
 * Class Name: BusinessException
 * Description: 비즈니스 로직 처리 중 발생하는 예외를 위한 커스텀 클래스
 * Example:
 *      // 서비스 계층에서 특정 조건에 맞지 않을 때 예외를 발생시킴
 *      if (!user.isPasswordMatching(password)) {
 *          throw new BusinessException(ResponseCode.INVALID_PASSWORD);
 *      }
 *
 * History
 * 2024/06/04  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 예외에 대한 상태 정보를 담는 {@link ResponseCode}
     */
    private final ResponseCode responseCode;

    /**
     * 에러 코드(ResponseCode)를 받아 예외를 생성
     *
     * @param responseCode 발생할 예외의 상태 정보를 담은 ResponseCode Enum
     */
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}
