package com.dxh.ShopappBe.entity;

import com.dxh.ShopappBe.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity<Long> {

    @Column(unique = true, nullable = false)
    String username;

    @Enumerated(EnumType.STRING)
    Gender gender;

    String fullName;

    @Column(unique = true, nullable = false)
    String email;

    String avatar;

    @Column(unique = true, nullable = false)
    String phoneNumber;

    String password;

    @Column(name = "enabled")
    Boolean enabled;

    @DateTimeFormat(pattern = "yyyy/MM/dd")
    LocalDate dob;

    @ManyToMany
    Set<Role> roles;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "user")
    Set<Address> addresses;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "user")
    Set<Order> orders;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "user")
    Set<Review> reviews;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<VerificationToken> verificationToken;

}