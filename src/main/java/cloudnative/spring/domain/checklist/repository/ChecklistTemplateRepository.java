package cloudnative.spring.domain.checklist.repository;

import cloudnative.spring.domain.checklist.entity.ChecklistTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChecklistTemplateRepository extends JpaRepository<ChecklistTemplate, String> {

    /**
     * 시스템 템플릿만 조회 (기본 제공 템플릿)
     */
    List<ChecklistTemplate> findByIsSystemTemplateOrderByCreatedAtAsc(Boolean isSystemTemplate);

    /**
     * 사용자가 생성한 템플릿 조회
     */
    List<ChecklistTemplate> findByCreatedByOrderByCreatedAtDesc(String createdBy);

    /**
     * 템플릿 항목과 함께 조회
     */
    @Query("SELECT t FROM ChecklistTemplate t LEFT JOIN FETCH t.items WHERE t.id = :id")
    Optional<ChecklistTemplate> findByIdWithItems(@Param("id") String id);
}