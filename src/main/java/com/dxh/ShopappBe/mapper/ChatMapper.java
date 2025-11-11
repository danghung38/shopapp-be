package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.ChatMessageResponse;
import com.dxh.ShopappBe.entity.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "timestamp", source = "createdAt")
    @Mapping(target = "to", source = "recipient")
    @Mapping(target = "from", source = "sender")
    ChatMessageResponse chatToChatMessageResponse(Chat chat);
}
