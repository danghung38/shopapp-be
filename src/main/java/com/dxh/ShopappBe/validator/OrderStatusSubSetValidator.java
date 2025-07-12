package com.dxh.ShopappBe.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class OrderStatusSubSetValidator implements ConstraintValidator<OrderStatusSubset, String> {
    private String[] orderStatusValues;

    @Override
    public void initialize(OrderStatusSubset constraint) {
        // Lưu danh sách giá trị hợp lệ dưới dạng String
        orderStatusValues = Arrays.stream(constraint.anyOf())
                .map(Enum::name) // Chuyển Enum thành String
                .toArray(String[]::new);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // Cho phép null
        return Arrays.stream(orderStatusValues)
                .anyMatch(g -> g.equalsIgnoreCase(value)); // Kiểm tra giá trị
    }
}
