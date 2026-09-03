package seungeasy.crewnavigator.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import seungeasy.crewnavigator.common.response.CustomResponse;
import seungeasy.crewnavigator.common.response.ResponseCode;

/**
 * <pre>
 * Class Name: GlobalExceptionHandler
 * Description: 프로젝트 전역에서 발생하는 예외를 처리하기 위한 클래스
 * Example:
 *      // 서비스에서 BusinessException이 발생하면,
 *      // 이 클래스의 handleBusinessException 메서드가 예외를 잡아
 *      // HTTP 상태 코드와 에러 응답을 클라이언트에게 반환합니다.
 *
 * History
 * 2024/06/04  Seung-Geon: Class 생성 및 Javadoc 추가
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * <pre>
     * 비즈니스 예외 처리
     *
     * - {@link BusinessException} 타입의 예외를 처리합니다.
     * - 서비스 로직 상의 예외 상황(예: 유효하지 않은 요청, 데이터 불일치 등)을 담당합니다.
     * - 예외에 포함된 {@link ResponseCode}를 기반으로 에러 응답을 생성합니다.
     *</pre>
     *
     * @param e 발생한 BusinessException
     * @return 에러 정보를 담은 ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CustomResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage(), e);
        ResponseCode responseCode = e.getResponseCode();
        return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(CustomResponse.error(responseCode));
    }

    /**
     * Spring Security 권한 위반 예외 처리
     *
     * - {@code @PreAuthorize} 등으로 보호된 리소스에 권한이 없는 사용자가 접근할 때 발생합니다.
     * - 예: 일반 회원이 어드민 전용 API(AdminGroupController)를 호출하는 경우
     * - 항상 403 Forbidden을 반환합니다.
     *
     * @param e 발생한 AccessDeniedException
     * @return 403 에러 응답을 담은 ResponseEntity
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<CustomResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("AccessDeniedException: {}", e.getMessage());
        ResponseCode responseCode = ResponseCode.FORBIDDEN;
        return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(CustomResponse.error(responseCode));
    }

    /**
     * 요청 본문 검증 실패 예외 처리
     *
     * - {@code @Valid @RequestBody} DTO의 필드 검증(@NotBlank 등)이 실패할 때 발생합니다.
     * - 예: GroupWarningRequestDto.warningReason이 빈 값인 경우
     * - 항상 400 Bad Request(EC001)를 반환하며, 첫 번째 실패 필드의 메시지를 포함합니다.
     *
     * @param e 발생한 MethodArgumentNotValidException
     * @return 400 에러 응답을 담은 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CustomResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse(ResponseCode.INVALID_INPUT_VALUE.getMessage());
        return ResponseEntity
                .status(ResponseCode.INVALID_INPUT_VALUE.getHttpStatus())
                .body(CustomResponse.error(ResponseCode.INVALID_INPUT_VALUE));
    }

    /**
     * <pre>
     * 예상치 못한 예외 처리
     *
     * - 위에서 처리되지 않은 모든 {@link Exception} 타입의 예외를 처리합니다.
     * - 시스템 레벨의 오류나 개발자가 예측하지 못한 예외 상황을 담당합니다.
     * - 항상 500 Internal Server Error를 반환합니다.
     *</pre>
     *
     * @param e 발생한 Exception
     * @return 500 에러 응답을 담은 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CustomResponse<Void>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);
        ResponseCode responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(responseCode.getHttpStatus())
                .body(CustomResponse.error(responseCode));
    }

}
