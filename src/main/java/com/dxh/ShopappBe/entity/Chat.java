package com.dxh.ShopappBe.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chats")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    String sender; // username hoặc ID người gửi (user hoặc admin)

    @Column(nullable = false)
    String recipient; // người nhận (luôn là admin hoặc user)

    @Column(nullable = false)
    LocalDateTime createAt;

    @Column(nullable = false)
    boolean isRead = false; // đã đọc hay chưa (cho admin đọc sau)

}
