package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.AddressCreateRequest;
import com.dxh.ShopappBe.dto.request.AddressUpdateRequest;
import com.dxh.ShopappBe.dto.response.AddressResponse;
import com.dxh.ShopappBe.entity.Address;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.AddressMapper;
import com.dxh.ShopappBe.repo.AddressRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.interfac.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressServiceImpl implements AddressService {
    UserRepository userRepository;
    AddressRepository addressRepository;
    AddressMapper addressMapper;

    @Override
    public AddressResponse create(AddressCreateRequest addr) {
        User user = checkUser();
        checkDuplicateAddress(user, addr);

        Set<Address> existing = addressRepository.findByUser_IdAndEnabledTrue(user.getId());

        Address newAddress = addressMapper.toAddress(addr);
        newAddress.setUser(user);
        newAddress.setEnabled(true);
        if (existing.isEmpty()) {
            newAddress.setIsDefault(true);
        }
        addressRepository.save(newAddress);
        return addressMapper.toAddressResponse(newAddress);
    }

    @Override
    public Set<AddressResponse> getMyAddressList() {
        return addressRepository.findByUser_IdAndEnabledTrue(checkUser().getId()).stream()
                .map(addressMapper::toAddressResponse).collect(Collectors.toSet());
    }

    @Override
    public AddressResponse update(AddressUpdateRequest addr, Long addrId) {
        User user = checkUser();
        Address address = addressRepository.findByIdAndUser_IdAndEnabledTrue(addrId, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXITSTED));

        address.setFullName(addr.getFullName());
        address.setPhone(addr.getPhone());
        address.setCity(addr.getCity());
        address.setDistrict(addr.getDistrict());
        address.setWards(addr.getWards());
        address.setSpecificAddress(addr.getSpecificAddress());
        addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    /**
     * Soft delete: chỉ tắt enabled, KHÔNG xoá khỏi DB.
     * Nhờ vậy lịch sử đơn hàng cũ vẫn giữ được thông tin địa chỉ giao hàng.
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long addrId) {
        User user = checkUser();
        Address address = addressRepository.findByIdAndUser_IdAndEnabledTrue(addrId, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXITSTED));
        address.setEnabled(false);
        // Nếu địa chỉ bị xoá là mặc định thì cần đặt lại mặc định cho địa chỉ khác (nếu có)
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            address.setIsDefault(false);
            addressRepository.findByUser_IdAndEnabledTrue(user.getId()).stream()
                    .filter(a -> !a.getId().equals(addrId))
                    .findFirst()
                    .ifPresent(a -> {
                        a.setIsDefault(true);
                        addressRepository.save(a);
                    });
        }
        addressRepository.save(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressResponse changeDefault(Long id) {
        User user = checkUser();

        addressRepository.findByUser_IdAndIsDefaultTrueAndEnabledTrue(user.getId())
                .ifPresent(oldAddress -> {
                    oldAddress.setIsDefault(false);
                    addressRepository.save(oldAddress);
                });

        Address address = addressRepository.findByIdAndUser_IdAndEnabledTrue(id, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXITSTED));
        address.setIsDefault(true);
        addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    @Override
    public AddressResponse getMyAddressById(Long id) {
        User user = checkUser();
        Address address = addressRepository.findByIdAndUser_IdAndEnabledTrue(id, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXITSTED));
        return addressMapper.toAddressResponse(address);
    }

    private User checkUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        return userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private void checkDuplicateAddress(User user, AddressCreateRequest addr) {
        boolean exists = addressRepository
                .existsByUser_IdAndCityAndDistrictAndWardsAndSpecificAddressAndFullNameAndEnabledTrue(
                        user.getId(),
                        addr.getCity(),
                        addr.getDistrict(),
                        addr.getWards(),
                        addr.getSpecificAddress(),
                        addr.getFullName()
                );
        if (exists) {
            throw new AppException(ErrorCode.ADDRESS_DUPLICATE);
        }
    }
}
