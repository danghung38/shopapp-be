package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.AddressCreateRequest;
import com.dxh.ShopappBe.dto.request.AddressUpdateRequest;
import com.dxh.ShopappBe.dto.response.AddressResponse;


import java.util.Set;

public interface AddressService {
    AddressResponse create(AddressCreateRequest addr);

    Set<AddressResponse> getMyAddressList();

    AddressResponse update(AddressUpdateRequest addr, Long id);

    void delete(Long addrId);

    AddressResponse changeDefault(Long id);

    AddressResponse getMyAddressById(Long id);
}
