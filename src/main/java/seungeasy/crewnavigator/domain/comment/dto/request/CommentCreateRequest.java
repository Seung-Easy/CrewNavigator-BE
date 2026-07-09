package seungeasy.crewnavigator.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <pre>
 * Class Name: CommentCreateRequest
 * Description: 댓글 생성 요청 DTO. 데이터베이스 테이블 제약조건을 기반으로 설계되었습니다.
 *
 * [주요 필드]
 * - postId: 댓글을 작성할 대상 게시글 고유 번호 (FK 제약조건용, 필수)
 * - content: 댓글 본문 내용 (NOT NULL, 필수)
 *
 * History
 * 2026.07.05: Chi-Yoon: DB 스키마 기반 댓글 생성 DTO 최초 생성
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
public record CommentCreateRequest(
        @NotNull(message = "댓글을 작성할 게시글 번호는 필수입니다.")
        Long postId,

        @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
        String content
) {}