package com.security.training.step2.repository;

import com.security.training.step2.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    List<Authority> findByUserId(Long userId);
}
