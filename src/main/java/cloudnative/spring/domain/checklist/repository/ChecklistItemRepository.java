package cloudnative.spring.domain.checklist.repository;

import cloudnative.spring.domain.checklist.entity.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, String> {

    /**
     * 특정 체크리스트의 항목 조회 (순서대로)
     */
    List<ChecklistItem> findByChecklistIdOrderByDisplayOrderAsc(String checklistId);

    /**
     * 특정 체크리스트의 항목 삭제
     */
    void deleteByChecklistId(String checklistId);
}