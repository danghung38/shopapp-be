package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.DiscountRequest;
import com.dxh.ShopappBe.dto.response.DiscountResponse;
import com.dxh.ShopappBe.entity.Discount;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.DiscountMapper;
import com.dxh.ShopappBe.repo.DiscountRepository;
import com.dxh.ShopappBe.service.interfac.DiscountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DiscountServiceImpl implements DiscountService {
    DiscountRepository discountRepository;
    DiscountMapper discountMapper;

    @Override
    public List<DiscountResponse> getAll() {
        return discountRepository.findAll().stream()
                .map(discountMapper::toDiscountResponse).toList();
    }

    @Override
    public DiscountResponse createDiscount(DiscountRequest discountRequest) {
        if(discountRepository.existsByName(discountRequest.getName())) {
            throw new AppException(ErrorCode.DISCOUNT_DUPLICATE);
        }
        return discountMapper.toDiscountResponse(discountRepository.save(discountMapper.toDiscount(discountRequest)));
    }


    @Override
    public void deleteDiscount(Long discountId) {
        if(!discountRepository.existsById(discountId)) {
            throw new AppException(ErrorCode.DISCOUNT_NOT_EXISTED);
        }
        discountRepository.deleteById(discountId);
    }

    @Override
    public void changeStatus(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_EXISTED));
        discount.setIsActive(!discount.getIsActive());
        discountRepository.save(discount);
    }

    @Override
    public DiscountResponse updateQuantity(Long id, Long quantity) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_EXISTED));
        if (quantity == null || quantity < 0) {
            throw new AppException(ErrorCode.QUANTITY_INVALID);
        }
        discount.setQuantity(quantity);
        return discountMapper.toDiscountResponse(discountRepository.save(discount));
    }


}
