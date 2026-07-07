package seungeasy.crewnavigator.domain.post.service;

import seungeasy.crewnavigator.domain.post.dto.request.PostCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostUpdateRequest; // 추가

/**
 * <pre>
 * Interface Name: PostCommandService
 * Description: 게시글(Post) 관련 쓰기(Command) 작업을 정의한 서비스 인터페이스.
 *
 * [제공 기능]
 * - 게시글 등록
 * - 게시글 수정 (추가)
 * - 게시글 삭제 (추가)
 *
 * History
 * 2026.06.27: Seung-Geon: 게시글 등록 쓰기 작업 인터페이스 생성
 * 2026.06.27: Chi-Yoon: 타입 변경에 따른 자바독 주석 최적화 및 불필요한 import 제거
 * 2026.07.01: Chi-Yoon: 게시글 수정(updatePost) 및 삭제(deletePost) 메서드 추가
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.2
 */
public interface PostCommandService {

    /**
     * 새로운 게시글을 작성하고 데이터베이스에 저장합니다.
     *
     * @param request   포스트맨(클라이언트)이 보낸 제목, 내용, 카테고리 ID가 담긴 DTO
     * @param userId    현재 로그인한 사용자의 고유 ID 문자열
     * @return 저장된 게시글의 고유 번호 (post_id)
     */
    Long createPost(PostCreateRequest request, String userId);

    /**
     * 기존 게시글을 수정합니다. (작성자 검증 포함)
     *
     * @param postId    수정할 게시글 고유 번호
     * @param request   수정할 제목과 내용이 담긴 DTO
     * @param userId    현재 수정을 요청한 로그인 사용자의 고유 ID 문자열
     */
    void updatePost(Long postId, PostUpdateRequest request, String userId);

    /**
     * 기존 게시글을 삭제(소프트 딜리트)합니다. (작성자 검증 포함)
     *
     * @param postId    삭제할 게시글 고유 번호
     * @param userId    현재 삭제를 요청한 로그인 사용자의 고유 ID 문자열
     */
    void deletePost(Long postId, String userId);
}