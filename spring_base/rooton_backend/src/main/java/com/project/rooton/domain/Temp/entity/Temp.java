package com.project.rooton.domain.Temp.entity;

import com.project.rooton.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "temp")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Temp extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // createdAt,updatedAt는 BaseTimeEntity에서 제공
}