package cloudnative.spring.domain.task.entity;

import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "task_reflections")
public class TaskReflection extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "work_session_id")
    private Long workSessionId;

    @Column(name = "warmup_note", columnDefinition = "TEXT")
    private String warmupNote;  // 시작 전 다짐

    @Column(name = "cooldown_note", columnDefinition = "TEXT")
    private String cooldownNote;  // 완료 후 후기

    @Column(name = "category_name")
    private String categoryName;  // 나중에 카테고리별 모아보기용
}