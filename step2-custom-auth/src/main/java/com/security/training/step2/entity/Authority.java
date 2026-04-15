package com.security.training.step2.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 권한 엔티티
 */
@Entity
@Table(name = "authorities")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String authority;
}
