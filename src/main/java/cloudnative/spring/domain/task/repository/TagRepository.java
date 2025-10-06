package cloudnative.spring.domain.task.repository;

import cloudnative.spring.domain.task.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByCategoryId(String categoryId);
}