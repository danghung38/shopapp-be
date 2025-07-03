package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.PermissionRequest;
import com.dxh.ShopappBe.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);

    List<PermissionResponse> getAll();

    void delete(String permission);
}
