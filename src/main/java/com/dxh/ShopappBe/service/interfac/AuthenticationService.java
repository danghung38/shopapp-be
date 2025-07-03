package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.AuthenticationRequest;
import com.dxh.ShopappBe.dto.request.IntrospectRequest;
import com.dxh.ShopappBe.dto.request.LogoutRequest;
import com.dxh.ShopappBe.dto.request.RefreshRequest;
import com.dxh.ShopappBe.dto.response.AuthenticationResponse;
import com.dxh.ShopappBe.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    //    kiểm tra token
    IntrospectResponse introspect(IntrospectRequest request)
                throws JOSEException, ParseException;

    //đăng nhập và tạo token
    AuthenticationResponse authenticate(AuthenticationRequest request);

    //token sắp hết hạn thì gia hạn
    AuthenticationResponse refreshToken(RefreshRequest request)
            throws ParseException, JOSEException;

    void logout(LogoutRequest request) throws ParseException, JOSEException;
}
