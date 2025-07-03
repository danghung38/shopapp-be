package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.response.CartResponse;
import com.dxh.ShopappBe.entity.Cart;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.CartMapper;
import com.dxh.ShopappBe.repo.CartRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.interfac.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    UserRepository userRepository;
    CartMapper cartMapper;

    @Override
    public CartResponse getCart() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return cartMapper.toCartResponse(cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_EXISTED)));
    }
}
