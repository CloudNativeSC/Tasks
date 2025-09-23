package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String>{

    //1.정렬 순서대로 카테고리 조회
    List<Category> findAllByOrderBySortOrderAsc();

    //2.이름으로 카테고리 찾기
    Optional<Category> findByName(String name);

    //3.특정 이름이 존재하는지 확인
    boolean existsByName(String name);
}
