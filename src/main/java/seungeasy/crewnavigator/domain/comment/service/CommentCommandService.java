package seungeasy.crewnavigator.domain.comment.service;

import seungeasy.crewnavigator.domain.comment.dto.request.CommentCreateRequest;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentUpdateRequest;

/**
 * <pre>
 * Interface Name: CommentCommandService
 * Description: 댓글(Comment) 관련 쓰기(Command) 작업을 정의한 서비스 인터페이스.
 *
 * [제공 기능]
 * - 댓글 등록
 * - 댓글 수정
 * - 댓글 삭제
 * - 댓글 강제 삭제 (ADMIN)
 *
 * History
 * 2026.07.05: Chi-Yoon: 패키지 구조 스펙 기반 댓글 등록 인터페이스 최초 생성
 * 2026.07.25: Chi-Yoon: 관리자 전용 댓글 강제 삭제(forceDeleteCommentByAdmin) 메서드 추가
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.1
 */
public interface CommentCommandService {

    /**
     * 새로운 댓글을 등록하고 데이터베이스에 저장합니다.
     *
     * @param request   댓글 작성을 요청한 게시글 번호와 내용이 담긴 DTO
     * @param userId    현재 댓글을 작성하는 로그인 사용자의 고유 ID 문자열
     * @return 저장된 댓글의 고유 번호 (comment_id)
     */
    Long createComment(CommentCreateRequest request, String userId);

    /**
     * 기존 댓글의 내용을 수정합니다. (🔒 원작자 검증 포함)
     */
    void updateComment(Long commentId, CommentUpdateRequest request, String userId);

    /**
     * 기존 댓글을 소프트 딜리트 처리합니다. (🔒 원작자 검증 포함)
     */
    void deleteComment(Long commentId, String userId);

    /**
     * [관리자 전용] 작성자 소유권 검증을 우회하여 댓글을 강제 삭제 처리합니다.
     *
     * @param commentId     강제 삭제할 댓글 고유 번호
     * @param adminUsername 수행하는 관리자의 계정 ID (감사/로깅용)
     */
    void forceDeleteCommentByAdmin(Long commentId, String adminUsername);
}