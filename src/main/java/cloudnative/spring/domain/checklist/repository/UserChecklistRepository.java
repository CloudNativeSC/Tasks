package cloudnative.spring.domain.checklist.repository;

import cloudnative.spring.domain.checklist.entity.UserChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChecklistRepository extends JpaRepository<UserChecklist, String> {

    /**
     * 사용자의 체크리스트 목록 (최신순)
     */
    List<UserChecklist> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * 완료된 체크리스트만 조회
     */
    @Query("SELECT c FROM UserChecklist c WHERE c.userId = :userId " +
            "AND c.completedAt IS NOT NULL ORDER BY c.completedAt DESC")
    List<UserChecklist> findCompletedChecklists(@Param("userId") String userId);

    /**
     * 진행 중인 체크리스트만 조회
     */
    @Query("SELECT c FROM UserChecklist c WHERE c.userId = :userId " +
            "AND c.completedAt IS NULL ORDER BY c.createdAt DESC")
    List<UserChecklist> findInProgressChecklists(@Param("userId") String userId);

    /**
     * 체크리스트 항목과 함께 조회
     */
    @Query("SELECT c FROM UserChecklist c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<UserChecklist> findByIdWithItems(@Param("id") String id);
}