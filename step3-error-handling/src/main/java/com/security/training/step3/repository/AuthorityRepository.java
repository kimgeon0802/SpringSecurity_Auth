package com.security.training.step3.repository;

import com.security.training.step3.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    List<Authority> findByUserId(Long userId);
}
