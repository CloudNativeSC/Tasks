package cloudnative.spring.domain.task.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskTagId implements Serializable {
    @Column(name = "task_id")
    private String taskId;

    @Column(name = "tag_id")
    private Long tagId;
}