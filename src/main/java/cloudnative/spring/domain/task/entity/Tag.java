package cloudnative.spring.domain.task.entity;


import cloudnative.spring.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags",
        uniqueConstraints = @UniqueConstraint(name="unique_tag_category", columnNames = {"name","category_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // BIGSERIAL

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "category_id", nullable = false, length = 255)
    private String categoryId;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "usage_count")
    private Integer usageCount;
}
