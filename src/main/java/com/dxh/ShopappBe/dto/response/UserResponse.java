package com.dxh.ShopappBe.dto.response;

import com.dxh.ShopappBe.entity.Role;
import com.dxh.ShopappBe.enums.Gender;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse implements Serializable {
    Long id;
    String username;
    String fullName;
    String email;
    String phoneNumber;
    String avatar;
    Gender gender;
    LocalDate dob;
    Boolean enabled;
    Set<Role> roles;
}
