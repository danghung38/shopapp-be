package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findByProductIdOrderByLevelAsc(Long productId);
    void deleteByProductId(Long productId);
}
