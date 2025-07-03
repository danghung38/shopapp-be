package com.dxh.ShopappBe.service.impl;


import com.dxh.ShopappBe.dto.request.CartItemCreateRequest;
import com.dxh.ShopappBe.dto.response.CartItemResponse;
import com.dxh.ShopappBe.entity.Cart;
import com.dxh.ShopappBe.entity.CartItem;
import com.dxh.ShopappBe.entity.Product;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.CartItemMapper;
import com.dxh.ShopappBe.repo.CartItemRepository;
import com.dxh.ShopappBe.repo.CartRepository;
import com.dxh.ShopappBe.repo.ProductRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.interfac.CartItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartItemServiceImpl implements CartItemService {
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;
    CartItemMapper cartItemMapper;
    UserRepository userRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartItemResponse addCartItem(CartItemCreateRequest cartItemCreateRequest) {

        Cart cart = cartRepository.findByUser_Id(checkUserId())
                .orElseThrow(()->new AppException(ErrorCode.CART_NOT_EXISTED));
        Product product = productRepository.findById(cartItemCreateRequest.getProductId())
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

//        nếu đã có ở trong giỏ hàng chỉ điều chỉnh số lượng và tổng tiền
        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), product.getId());
        if (existingCartItemOpt.isPresent()) {
            CartItem cartExist = existingCartItemOpt.get();
            if (product.getQuantity() < cartItemCreateRequest.getQuantity()) {
                throw new AppException(ErrorCode.INVALID_NUMBER_QUANTITY);
            }
            Double oldAmount = cartExist.getAmount();
            cartExist.setQuantity(cartItemCreateRequest.getQuantity());
            cartExist.setAmount(cartItemCreateRequest.getQuantity() * product.getPrice());


            CartItem updated = cartItemRepository.save(cartExist);
            cart.setTotal(cart.getTotal() - oldAmount + updated.getAmount());
            cartRepository.save(cart);
            return cartItemMapper.toCartItemResponse(updated);
        }


//      chưa có trong giỏ hàng
        if(product.getQuantity()<cartItemCreateRequest.getQuantity()){
            throw new AppException(ErrorCode.INVALID_NUMBER_QUANTITY);
        }
        CartItem cartItem = CartItem.builder()
                .quantity(cartItemCreateRequest.getQuantity())
                .product(product)
                .cart(cart)
                .amount(cartItemCreateRequest.getQuantity().doubleValue()*product.getPrice())
                .build();
        CartItem savedCartItem = cartItemRepository.save(cartItem);
        cart.setTotal(cart.getTotal()+savedCartItem.getAmount());
        cartRepository.save(cart);

        return cartItemMapper.toCartItemResponse(savedCartItem);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCartItem(Long cartItemId) {
        Long userId = checkUserId();

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        CartItem cartItemRemove = cartItemRepository.findByIdAndCart_Id(cartItemId, cart.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CARTITEM_NOT_EXISTED));

        double amount = cartItemRemove.getAmount();

        cart.setTotal(cart.getTotal() - amount);

        cartItemRepository.delete(cartItemRemove);
        cartRepository.save(cart);

    }

    private Long checkUserId(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user.getId();
    }
}
