package com.dxh.ShopappBe.mapper;



import com.dxh.ShopappBe.dto.request.PermissionRequest;
import com.dxh.ShopappBe.dto.response.PermissionResponse;
import com.dxh.ShopappBe.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
