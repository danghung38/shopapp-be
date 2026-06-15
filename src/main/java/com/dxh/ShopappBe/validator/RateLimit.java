package com.dxh.ShopappBe.validator;

import com.dxh.ShopappBe.enums.RateLimitEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    RateLimitEnum action();

    int maxRequests();

    int durationMinutes();
}