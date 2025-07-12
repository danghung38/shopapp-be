package com.dxh.ShopappBe.validator;

import com.dxh.ShopappBe.enums.OrderStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = OrderStatusSubSetValidator.class)
public @interface OrderStatusSubset {
    OrderStatus[] anyOf();
    String message() default "order status must be any of {PENDING_CONFIRMATION, CANCELED, DELIVERED, SHIPPING}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}