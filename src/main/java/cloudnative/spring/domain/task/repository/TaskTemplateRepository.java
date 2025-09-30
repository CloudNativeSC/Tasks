package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.TaskTemplate;
import cloudnative.spring.domain.task.enums.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTemplateRepository extends JpaRepository<TaskTemplate,String> {

    // 사용자별 템플릿 페이징 조회
    Page<TaskTemplate> findByUserIdOrderByUsageCountDesc(String userId, Pageable pageable);

    // 카테고리/종류로 필터
    Page<TaskTemplate> findByCategoryIdAndTemplateType(String categoryId, TemplateType type, Pageable pageable);

    // 이름 검색 (부분일치)
    Page<TaskTemplate> findByUserIdAndTemplateNameContainingIgnoreCase(
            String userId, String keyword, Pageable pageable
    );
}
