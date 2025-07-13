package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.entity.VerificationToken;
import com.dxh.ShopappBe.enums.VerifyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    Optional<VerificationToken> findBySecretKeyAndVerifyType(String secretKey, VerifyType verifyType);

    void deleteByUserAndVerifyType(User user, VerifyType verifyType);
}
