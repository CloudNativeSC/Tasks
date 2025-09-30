package cloudnative.spring.domain.task.entity;

import jakarta.persistence.*;
import lombok.*;
import cloudnative.spring.global.entity.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category extends BaseTimeEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "color_code", length = 7)
    @Builder.Default
    private String colorCode = "#007bff";

    @Column(name = "icon_image", columnDefinition = "TEXT")
    private String iconImage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    //테이블 관계

    // 1:N 관계 - 하나의 카테고리는 여러 Task를 가질 수 있음
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    // 1:N 관계 - 하나의 카테고리는 여러 TaskTemplate을 가질 수 있음
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskTemplate> taskTemplates = new ArrayList<>();

}