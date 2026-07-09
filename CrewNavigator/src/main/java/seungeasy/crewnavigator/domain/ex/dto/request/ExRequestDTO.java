package seungeasy.crewnavigator.domain.ex.dto.request;

/**
 * DTO는 기본 record로 만든다.
 *  이유: record의 불변성을 통한 에러 가능성을 줄임
 *
 *  단 다음과 같은 상황에 class로 변경
 *  1. 로직상 DTO 내부의 값이 변환해야 하는 경우
 *      record는 필드값이 불변성을 가지기 때문에 변경이 불가능함
 *  2. JPA Entity는 무조건 class로 변경
 *      @Entity는 제약상 record 불가능
 */
public record ExRequestDTO() {
}
