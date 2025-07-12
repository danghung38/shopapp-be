package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.AddressCreateRequest;
import com.dxh.ShopappBe.dto.request.AddressUpdateRequest;
import com.dxh.ShopappBe.dto.response.AddressResponse;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.service.interfac.AddressService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(method = "POST", summary = "Add new address",
            description = "Send a request via this API to create new address")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ApiResponse<AddressResponse> createAddress(@RequestBody AddressCreateRequest addr) {
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created address")
                .result(addressService.create(addr))
                .build();
    }


    @Operation(method = "GET", summary = "Get all my addresses",
            description = "Get all addresses of the currently logged-in user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @GetMapping("/list")
    public ApiResponse<Set<AddressResponse>> getAllMyAddress() {
        return ApiResponse.<Set<AddressResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully retrieved all addresses")
                .result(addressService.getMyAddressList())
                .build();
    }

    @Operation(method = "GET", summary = "Get my address by ID",
            description = "Get address of the currently logged-in user by ID")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @GetMapping
    public ApiResponse<AddressResponse> getMyAddress(@RequestParam Long id) {
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully get addresses by id")
                .result(addressService.getMyAddressById(id))
                .build();
    }

    @Operation(method = "PUT", summary = "Update my addresses",
            description = "Update addresses of the currently logged-in user")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable Long id, @RequestBody AddressUpdateRequest addr) {
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully updated address")
                .result(addressService.update(addr,id))
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @DeleteMapping("/{addrId}")
    public ApiResponse<?> delete(@PathVariable Long addrId) {
        addressService.delete(addrId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete address successful")
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @PatchMapping("/{id}/change-default")
    public ApiResponse<AddressResponse> changeDefault(@PathVariable Long id){
        return ApiResponse.<AddressResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully changed default address")
                .result(addressService.changeDefault(id))
                .build();
    }
}
