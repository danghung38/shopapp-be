package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Set<Address> findByUser_Id(Long userId);

    boolean existsByUser_IdAndCityAndDistrictAndWardsAndSpecificAddressAndFullName(Long id, String city, String district, String wards, String specificAddress, String fullName);

    Optional<Address> findByIdAndUser_Id(Long addrId, Long id);

    void deleteByIdAndUser_Id(Long addrId, Long userId);
    Optional<Address> findByUser_IdAndIsDefaultTrue(Long userId);

}
