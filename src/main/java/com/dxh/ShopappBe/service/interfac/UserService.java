package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.ForgotPasswordRequest;
import com.dxh.ShopappBe.dto.request.ResetPasswordRequest;
import com.dxh.ShopappBe.dto.request.UserCreationRequest;
import com.dxh.ShopappBe.dto.request.UserUpdateRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.UserResponse;
import com.dxh.ShopappBe.dto.response.UserUpdateResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreationRequest request);

    PageResponse<List<UserResponse>> getAllUsersSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<List<UserResponse>> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] role);

    void verifyRegister(String secretKey);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    UserUpdateResponse updateMyUser(UserUpdateRequest request, MultipartFile userImage);

    UserResponse getMyInfo();
}
