package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.UserCreationRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreationRequest request);

    PageResponse<List<UserResponse>> getAllUsersSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<List<UserResponse>> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] role);
}
