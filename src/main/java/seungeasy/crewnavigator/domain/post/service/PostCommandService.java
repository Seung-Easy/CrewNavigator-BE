package seungeasy.crewnavigator.domain.post.service;

import seungeasy.crewnavigator.domain.post.dto.request.NoticeCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.NoticeUpdateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostUpdateRequest;

/**
 * <pre>
 * Interface Name: PostCommandService
 * Description: 게시글(Post) 관련 쓰기(Command) 작업을 정의한 서비스 인터페이스.
 *
 * [제공 기능]
 * - 게시글 등록
 * - 게시글 수정
 * - 게시글 삭제
 * - 게시글 강제 삭제 (ADMIN)
 * - 공지사항 작성 (ADMIN)
 * - 공지사항 수정 (ADMIN)
 *
 * History
 * 2026.06.27: Seung-Geon: 게시글 등록 쓰기 작업 인터페이스 생성
 * 2026.06.27: Chi-Yoon: 타입 변경에 따른 자바독 주석 최적화 및 불필요한 import 제거
 * 2026.07.01: Chi-Yoon: 게시글 수정(updatePost) 및 삭제(deletePost) 메서드 추가
 * 2026.07.25: Chi-Yoon: 관리자 전용 게시글 강제 삭제(forceDeletePostByAdmin) 메서드 추가
 * 2026.07.28: Chi-Yoon: 관리자 전용 공지사항 작성(createNoticeByAdmin) 및 수정(updateNoticeByAdmin) 메서드 추가
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.4
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

    /**
     * [관리자 전용] 작성자 소유권 검증을 우회하여 게시글을 강제 삭제 처리합니다.
     *
     * @param postId        강제 삭제할 게시글 고유 번호
     * @param adminUsername 수행하는 관리자의 계정 ID (감사/로깅용)
     */
    void forceDeletePostByAdmin(Long postId, String adminUsername);

    /**
     * [관리자 전용] 새로운 공지사항을 작성하고 데이터베이스에 저장합니다.
     *
     * @param request       공지사항 제목과 내용이 담긴 DTO
     * @param adminUsername 공지사항을 작성하는 관리자의 계정 ID
     * @return 저장된 공지사항의 고유 번호 (post_id)
     */
    Long createNoticeByAdmin(NoticeCreateRequest request, String adminUsername);

    /**
     * [관리자 전용] 기존 공지사항을 수정합니다.
     *
     * @param noticeId      수정할 공지사항 고유 번호
     * @param request       수정할 공지사항 제목과 내용이 담긴 DTO
     * @param adminUsername 수정을 요청한 관리자의 계정 ID
     */
    void updateNoticeByAdmin(Long noticeId, NoticeUpdateRequest request, String adminUsername);
}