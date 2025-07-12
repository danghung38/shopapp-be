package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // Lấy toàn bộ tin nhắn giữa 2 người (có thể là user với admin)
    @Query("""
    SELECT c FROM Chat c
    WHERE (c.sender = :sender AND c.recipient = :recipient)
       OR (c.sender = :recipient AND c.recipient = :sender)
    ORDER BY c.createAt ASC
""")
    List<Chat> findChatHistory(@Param("sender") String sender, @Param("recipient") String recipient);

    List<Chat> findAllBySenderOrRecipient(String sender, String recipient);

}
