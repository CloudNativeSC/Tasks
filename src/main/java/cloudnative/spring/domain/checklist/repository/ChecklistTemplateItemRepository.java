package cloudnative.spring.domain.checklist.repository;

import cloudnative.spring.domain.checklist.entity.ChecklistTemplateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistTemplateItemRepository extends JpaRepository<ChecklistTemplateItem, String> {

    /**
     * 특정 템플릿의 항목 조회 (순서대로)
     */
    List<ChecklistTemplateItem> findByTemplateIdOrderByDisplayOrderAsc(String templateId);

    /**
     * 특정 템플릿의 항목 삭제
     */
    void deleteByTemplateId(String templateId);
}