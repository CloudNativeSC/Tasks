package cloudnative.spring.domain.task.entity;

import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTag {

    // ✅ 복합 PK를 임베디드로 보관
    @EmbeddedId
    private cloudnative.spring.domain.tag.entity.TaskTagId id;

    // ✅ task_id(FK) ↔ id.taskId 를 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // ✅ tag_id(FK) ↔ id.tagId 를 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // 스키마에 created_at만 있고 updated_at은 없으므로 BaseTimeEntity 상속하지 않음
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
