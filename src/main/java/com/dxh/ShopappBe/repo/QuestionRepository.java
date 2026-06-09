package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.Question;
import com.dxh.ShopappBe.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findByProductId(Long productId, Pageable pageable);
    Page<Question> findByUserId(Long userId, Pageable pageable);
    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);
}
