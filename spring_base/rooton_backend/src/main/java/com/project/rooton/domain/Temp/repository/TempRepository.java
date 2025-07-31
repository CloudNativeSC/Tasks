package com.project.rooton.domain.Temp.repository;

import com.project.rooton.domain.Temp.entity.Temp;  // Temp 클래스 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempRepository extends JpaRepository<Temp, Long> {
}