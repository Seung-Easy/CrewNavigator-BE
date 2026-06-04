package seungeasy.crewnavigator.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * <pre>
 * Class Name: CustomResponse
 * Description: API 공통 응답 포맷을 위한 클래스
 * Example:
 *      // 1. 단일 객체 응답
 *      {
 *          "code": "S001",
 *          "message": "요청이 성공적으로 처리되었습니다.",
 *          "data": {
 *              "id": 1,
 *              "name": "홍길동"
 *          }
 *      }
 *
 *      // 2. 페이징된 목록 응답
 *      {
 *          "code": "S001",
 *          "message": "요청이 성공적으로 처리되었습니다.",
 *          "data": {
 *              "content": [
 *                  { "id": 1, "title": "게시글 1" },
 *                  { "id": 2, "title": "게시글 2" }
 *              ],
 *              "pageNumber": 0,
 *              "totalPages": 5
 *          }
 *      }
 *
 *      // 3. 실패 응답
 *      {
 *          "code": "U001",
 *          "message": "해당 사용자를 찾을 수 없습니다."
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
@JsonInclude(JsonInclude.Include.NON_NULL) // 데이터가 null일 경우 JSON 응답에서 제외
public class CustomResponse<T> {

    private final String code;
    private final String message;
    private final T data;

    // 정적 팩토리 메서드를 통해 생성하도록 생성자는 private으로 제한
    private CustomResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 성공 응답 (데이터 포함)
     * @param responseCode 성공 코드 (ResponseCode Enum)
     * @param data         반환할 데이터
     */
    public static <T> CustomResponse<T> success(ResponseCode responseCode, T data) {
        return new CustomResponse<>(responseCode.getCode(), responseCode.getMessage(), data);
    }

    /**
     * 성공 응답 (데이터 없음)
     * @param responseCode 성공 코드 (ResponseCode Enum)
     */
    public static <T> CustomResponse<T> success(ResponseCode responseCode) {
        return new CustomResponse<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    /**
     * 실패 응답
     * @param responseCode 실패 코드 (ResponseCode Enum)
     */
    public static <T> CustomResponse<T> error(ResponseCode responseCode) {
        return new CustomResponse<>(responseCode.getCode(), responseCode.getMessage(), null);
    }
}
