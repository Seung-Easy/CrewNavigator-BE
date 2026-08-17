package seungeasy.crewnavigator.domain.post.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostType {

    GENERAL("일반 게시글"),
    NOTICE("공지사항");

    private final String description;
}