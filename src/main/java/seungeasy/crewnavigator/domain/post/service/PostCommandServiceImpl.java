package seungeasy.crewnavigator.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.common.exception.BusinessException;
import seungeasy.crewnavigator.common.response.ResponseCode;
import seungeasy.crewnavigator.domain.auth.entity.User;
import seungeasy.crewnavigator.domain.auth.repository.UserRepository;
import seungeasy.crewnavigator.domain.post.dto.request.NoticeCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.NoticeUpdateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostCreateRequest;
import seungeasy.crewnavigator.domain.post.dto.request.PostUpdateRequest;
import seungeasy.crewnavigator.domain.post.entity.Category;
import seungeasy.crewnavigator.domain.post.entity.Post;
import seungeasy.crewnavigator.domain.post.entity.PostType;
import seungeasy.crewnavigator.domain.post.repository.CategoryRepository;
import seungeasy.crewnavigator.domain.post.repository.PostRepository;

/**
 * <pre>
 * Class Name: PostCommandServiceImpl
 * Description: 게시글(Post) 관련 쓰기(Command) 작업을 처리하는 서비스 구현체.
 *
 * History
 * 2026.06.27: Chi-Yoon: UserRepository의 PK(String) 기반 findById 적용 및 회원 검증 로직 반영
 * 2026.06.27: Chi-Yoon: post 도메인 패키지 내부로 카테고리(Category) 관련 import 경로 전면 수정
 * 2026.07.01: Chi-Yoon: 게시글 수정/삭제 기능 구현 및 원작자 권한 검증(EP002) 로직 추가
 * 2026.07.25: Chi-Yoon: 관리자 전용 게시글 강제 삭제(forceDeletePostByAdmin) 메서드 구현
 * 2026.07.28: Chi-Yoon: 관리자 전용 공지사항 작성(createNoticeByAdmin) 및 수정(updateNoticeByAdmin) 메서드 구현
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.5
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Long createPost(PostCreateRequest request, String userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(ResponseCode.CATEGORY_NOT_FOUND));

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .writer(writer)
                .category(category)
                .postType(PostType.GENERAL) // 일반 게시글 지정
                .build();

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully. Post ID: {}, Category: {}", savedPost.getId(), category.getCategoryName());

        return savedPost.getId();
    }

    @Override
    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, String userId) {
        // 1. 게시글 존재 여부 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR)); // 임시로 서버에러 혹은 전용 에러 매핑

        // 2. 권한 검증: 게시글 작성자의 ID와 현재 로그인한 유저의 ID가 일치하는지 비교
        if (!post.getWriter().getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_POST_WRITER); // 아까 만든 EP002 에러!
        }

        // 3. 더티 체킹(Dirty Checking)을 통한 수정 연산 수행 (엔티티 내 update 메서드 호출)
        post.update(request.title(), request.content());
        log.info("Post updated successfully. Post ID: {} by User: {}", postId, userId);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String userId) {
        // 1. 게시글 존재 여부 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR));

        // 2. 권한 검증
        if (!post.getWriter().getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_POST_WRITER);
        }

        // 3. 소프트 딜리트 수행 (엔티티 내 delete 메서드 호출 -> is_deleted = 'Y')
        post.delete();
        log.info("Post soft-deleted successfully. Post ID: {} by User: {}", postId, userId);
    }

    @Override
    @Transactional
    public void forceDeletePostByAdmin(Long postId, String adminUsername) {
        log.warn("ADMIN ACTION - Force deleting post ID: {} requested by Admin: {}", postId, adminUsername);

        // 1. 게시글 존재 여부 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR));

        // 2. 작성자 검증(NOT_POST_WRITER)을 스킵하고 바로 소프트 딜리트 수행
        post.delete();
        log.info("Post force-deleted by Admin successfully. Post ID: {}, Admin: {}", postId, adminUsername);
    }

    @Override
    @Transactional
    public Long createNoticeByAdmin(NoticeCreateRequest request, String adminUsername) {
        log.info("ADMIN ACTION - Creating Notice by Admin: {}", adminUsername);

        // 1. 관리자 유저 존재 여부 검증
        User admin = userRepository.findById(adminUsername)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        // 2. PostType.NOTICE 타입으로 엔티티 생성
        Post notice = Post.builder()
                .title(request.title())
                .content(request.content())
                .writer(admin)
                .postType(PostType.NOTICE)
                .build();

        Post savedNotice = postRepository.save(notice);
        log.info("Notice created successfully. Notice ID: {} by Admin: {}", savedNotice.getId(), adminUsername);

        return savedNotice.getId();
    }

    @Override
    @Transactional
    public void updateNoticeByAdmin(Long noticeId, NoticeUpdateRequest request, String adminUsername) {
        log.info("ADMIN ACTION - Updating Notice ID: {} by Admin: {}", noticeId, adminUsername);

        // 1. 공지사항 존재 여부 조회
        Post notice = postRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ResponseCode.INTERNAL_SERVER_ERROR));

        // 2. 더티 체킹을 통한 내용 수정
        notice.update(request.title(), request.content());
        log.info("Notice updated successfully. Notice ID: {} by Admin: {}", noticeId, adminUsername);
    }
}