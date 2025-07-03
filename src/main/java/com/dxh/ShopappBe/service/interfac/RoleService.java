package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.RoleRequest;
import com.dxh.ShopappBe.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);

    List<RoleResponse> getAll();

    void delete(Long role);
}
