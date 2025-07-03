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
        checkDuplicateAddress(user,addr);

        Set<Address> address = addressRepository.findByUser_Id(user.getId());

        Address newAddress = addressMapper.toAddress(addr);
        newAddress.setUser(user);
        if(address.isEmpty()){
           newAddress.setIsDefault(true);
        }
        addressRepository.save(newAddress);
        return addressMapper.toAddressResponse(newAddress);
    }

    @Override
    public Set<AddressResponse> getMyAddressList() {
        return addressRepository.findByUser_Id(checkUser().getId()).stream()
                .map(addressMapper::toAddressResponse).collect(Collectors.toSet());
    }

    @Override
    public AddressResponse update(AddressUpdateRequest addr, Long addrId) {
        User user = checkUser();
        Address address = addressRepository.findByIdAndUser_Id(addrId,user.getId())
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long addrId) {
        addressRepository.deleteByIdAndUser_Id(addrId,checkUser().getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddressResponse changeDefault(Long id) {
        User user = checkUser();

        // Tìm địa chỉ đang mặc định (nếu có)
        addressRepository.findByUser_IdAndIsDefaultTrue(user.getId())
                .ifPresent(oldAddress -> {
                    oldAddress.setIsDefault(false);
                    addressRepository.save(oldAddress);
                });

        Address address = addressRepository.findByIdAndUser_Id(id,user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXITSTED));
        address.setIsDefault(true);
        addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }

    @Override
    public AddressResponse getMyAddressById(Long id) {
        User user = checkUser();
        Address address= addressRepository.findByIdAndUser_Id(id,user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXITSTED));
        return addressMapper.toAddressResponse(address);
    }


    private User checkUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user;
    }

    private void checkDuplicateAddress(User user, AddressCreateRequest addr) {
        boolean exists = addressRepository.existsByUser_IdAndCityAndDistrictAndWardsAndSpecificAddressAndFullName(
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
