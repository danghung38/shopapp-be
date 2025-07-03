package com.dxh.ShopappBe.entity;


import com.dxh.ShopappBe.enums.QuestionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "questions")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question extends AbstractEntity<Long> {

    @Column(nullable = false, columnDefinition = "TEXT")
    String questionText;

    @Column(columnDefinition = "TEXT", nullable = false)
    String answerText;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    QuestionStatus status = QuestionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    User admin;
}