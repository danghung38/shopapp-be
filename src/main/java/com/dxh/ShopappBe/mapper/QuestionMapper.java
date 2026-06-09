package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.QuestionResponse;
import com.dxh.ShopappBe.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "admin.id", target = "adminId")
    @Mapping(source = "admin.fullName", target = "adminFullName")
    QuestionResponse toQuestionResponse(Question question);
}
