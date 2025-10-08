package cloudnative.spring.domain.task.entity;

import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;


@Entity
@Table(name = "encouragement_messages")
public class EncouragementMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;  // "오늘도 좋은 하루 되세요!"

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;  // INITIAL, MILESTONE, CONSISTENT

    @Column(name = "min_count")
    private Integer minCount;  // 이 메시지를 보여줄 최소 횟수
}