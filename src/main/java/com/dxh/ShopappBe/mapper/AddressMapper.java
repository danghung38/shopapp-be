package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.request.AddressCreateRequest;
import com.dxh.ShopappBe.dto.response.AddressResponse;
import com.dxh.ShopappBe.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressCreateRequest addressCreateRequest);
    AddressResponse toAddressResponse(Address address);
}
