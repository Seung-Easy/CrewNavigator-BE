package seungeasy.crewnavigator.domain.comment.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seungeasy.crewnavigator.domain.comment.dto.request.CommentSearchRequest;
import seungeasy.crewnavigator.domain.comment.dto.response.CommentResponse;
import seungeasy.crewnavigator.domain.comment.repository.CommentRepository;

/**
 * <pre>
 * Class Name: CommentQueryServiceImpl
 * Description: 댓글(Comment) 관련 읽기/조회(Query) 작업을 처리하는 서비스 구현체.
 * </pre>
 *
 * @author Chi-Yoon
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentResponse> searchComments(CommentSearchRequest request) {
        log.info("Searching comments with conditions - PostID: {}, UserID: {}",
                request.postId(), request.userId());

        return commentRepository.searchComments(request.postId(), request.userId())
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }
}