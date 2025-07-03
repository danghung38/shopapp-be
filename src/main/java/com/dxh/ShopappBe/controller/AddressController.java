package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.AddressCreateRequest;
import com.dxh.ShopappBe.dto.request.AddressUpdateRequest;
import com.dxh.ShopappBe.dto.response.AddressResponse;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.service.interfac.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressController {

    AddressService addressService;

    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@RequestBody AddressCreateRequest addr) {
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created address")
                .result(addressService.create(addr))
                .build();
    }

    @GetMapping("/list")
    public ApiResponse<Set<AddressResponse>> getAllMyAddress() {
        return ApiResponse.<Set<AddressResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully retrieved all addresses")
                .result(addressService.getMyAddressList())
                .build();
    }

    @GetMapping
    public ApiResponse<AddressResponse> getMyAddress(@RequestParam Long id) {
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully get addresses by id")
                .result(addressService.getMyAddressById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable Long id, @RequestBody AddressUpdateRequest addr) {
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully updated address")
                .result(addressService.update(addr,id))
                .build();
    }

    @DeleteMapping("/{addrId}")
    public ApiResponse<?> delete(@PathVariable Long addrId) {
        addressService.delete(addrId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete address successful")
                .build();
    }

    @PatchMapping("/{id}/change-default")
    public ApiResponse<AddressResponse> changeDefault(@PathVariable Long id){
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully changed default address")
                .result(addressService.changeDefault(id))
                .build();
    }
}
