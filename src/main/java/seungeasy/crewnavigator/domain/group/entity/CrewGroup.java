package seungeasy.crewnavigator.domain.group.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <pre>
 *  Class Name: CrewGroup
 *  Description: 그룹(모임) 정보를 저장하는 엔티티.
 *
 *  [주요 필드]
 *  - groupId: 그룹 번호 (PK, AUTO_INCREMENT)
 *  - leaderId: 모임장 회원 아이디 (FK)
 *  - maxMembers: 그룹 정원 (기본 10)
 *  - isPrivate: 비공개 여부 (Y/N, Y면 그룹 검색에서 제외되는 비공개 그룹)
 *  - isDeleted: 삭제 여부 (Y/N, 소프트 삭제)
 *  - groupImage: 그룹 이미지 경로
 *
 * History
 * 2026.07.31: Seung-Geon: @GeneratedValue 추가 및 그룹 도메인 확장 컬럼(maxMembers, isDeleted, updatedAt, groupImage) 매핑
 * </pre>
 *
 * @author Seung-Geon
 * @version 1.1
 */
@Entity
@Table(name = "crew_group")
@Getter
@Setter
@NoArgsConstructor
public class CrewGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "leader_id", length = 50, nullable = false)
    private String leaderId;

    @Column(name = "group_name", length = 100, nullable = false)
    private String groupName;

    @Column(name = "max_members")
    private Long maxMembers = 10L;

    @Column(name = "description")
    private String description;

    @Column(name = "is_private", length = 1)
    private String isPrivate;

    @Column(name = "is_deleted", length = 1)
    private String isDeleted;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "group_image", length = 255)
    private String groupImage;

    /**
     * 엔티티 최초 저장 전 실행됩니다. createdAt과 기본값을 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.maxMembers == null) {
            this.maxMembers = 10L;
        }
        if (this.isPrivate == null) {
            this.isPrivate = "N";
        }
        if (this.isDeleted == null) {
            this.isDeleted = "N";
        }
    }

    /**
     * 엔티티 업데이트 전 실행됩니다. updatedAt을 현재 시간으로 갱신합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 그룹 공개/비공개 설정을 토글합니다.
     */
    public void changePrivate() {
        this.isPrivate = "Y".equals(this.isPrivate) ? "N" : "Y";
    }

    /**
     * 그룹 정보를 수정합니다.
     *
     * <pre>
     * 그룹장의 그룹 정보 수정 요청 시 호출되는 엔티티 메서드.
     * 전달된 값이 null이 아닌 경우에만 해당 필드를 갱신합니다 (부분 수정 지원).
     * 수정 일시(updatedAt)는 @PreUpdate에 의해 자동 갱신됩니다.
     * </pre>
     *
     * @param groupName  수정할 그룹명 (null이면 유지)
     * @param description 수정할 그룹 소개 (null이면 유지)
     * @param maxMembers  수정할 그룹 정원 (null이면 유지)
     * @param isPrivate   수정할 비공개 여부 "Y"/"N" (null이면 유지)
     * @param groupImage  수정할 그룹 이미지 경로 (null이면 유지)
     */
    public void update(String groupName, String description, Long maxMembers, String isPrivate, String groupImage) {
        if (groupName != null && !groupName.isBlank()) {
            this.groupName = groupName;
        }
        if (description != null) {
            this.description = description;
        }
        if (maxMembers != null && maxMembers > 0) {
            this.maxMembers = maxMembers;
        }
        if (isPrivate != null) {
            this.isPrivate = isPrivate;
        }
        if (groupImage != null) {
            this.groupImage = groupImage;
        }
    }

    /**
     * 그룹을 소프트 삭제 처리합니다.
     */
    public void delete() {
        this.isDeleted = "Y";
    }
}
