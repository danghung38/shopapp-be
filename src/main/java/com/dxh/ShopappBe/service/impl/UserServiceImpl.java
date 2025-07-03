package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.UserCreationRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.UserResponse;
import com.dxh.ShopappBe.entity.Cart;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.enums.Role;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.UserMapper;
import com.dxh.ShopappBe.repo.CartRepository;
import com.dxh.ShopappBe.repo.CustomSearchUserRepository;
import com.dxh.ShopappBe.repo.RoleRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.repo.specification.UserSpecificationsBuilder;
import com.dxh.ShopappBe.service.interfac.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dxh.ShopappBe.utils.AppConstant.SEARCH_SPEC_OPERATOR;
import static com.dxh.ShopappBe.utils.AppConstant.SORT_BY;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserMapper userMapper;
    CustomSearchUserRepository searchUserRepository;
    CartRepository cartRepository;

    static Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username", "email", "phoneNumber", "name");


    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse createUser(UserCreationRequest request){
        log.info("Creating user");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber()))
            throw new AppException(ErrorCode.PHONE_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRoles(Set.of(
                roleRepository.findByName(Role.USER.name())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED))
        ));

        User savedUser = userRepository.save(user);

//        tạo giỏ hàng
        Cart cart = Cart.builder()
                .user(savedUser)
                .build();
        cartRepository.save(cart);

        return userMapper.toUserResponse(savedUser);
    }

//    sort by one column
    @Override
    public PageResponse<List<UserResponse>> getAllUsersSortBy(int pageNo, int pageSize, String sortBy) {
        int page = pageNo>0?(pageNo-1):0;
        List<Sort.Order> sorts = new ArrayList<>();


        if (StringUtils.hasLength(sortBy)) {
            // name:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY); // AppConstant.SORT_BY = "(\\w+?)(:)(.*)"
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String field = matcher.group(1);
                String direction = matcher.group(3);
                if (!ALLOWED_SORT_FIELDS.contains(field)) {
                    throw new IllegalArgumentException("Invalid sort field: " + field);
                }
                if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
                    throw new IllegalArgumentException("Sort direction must be 'asc' or 'desc'");
                }

                if (direction.equalsIgnoreCase("asc")) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, field));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, field));
                }
            }
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));

        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> userResponseList = users.stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<List<UserResponse>>builder()
                .pageNo(page+1)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(userResponseList)
                .totalElements(users.getTotalElements())
                .build();
    }



    @Override
    public PageResponse<List<UserResponse>> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] role) {
        log.info("getUsersBySpecifications");

        if (user != null && role != null) {
            return searchUserRepository.searchUserByCriteriaWithJoin(pageable, user, role);
        } else if (user != null) {
            UserSpecificationsBuilder builder = new UserSpecificationsBuilder();

            Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
            for (String s : user) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }

            Page<User> users = userRepository.findAll(Objects.requireNonNull(builder.build()), pageable);
            List<UserResponse> userResponseList = users.stream().map(userMapper::toUserResponse).toList();

            return PageResponse.<List<UserResponse>>builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPage(users.getTotalPages())
                    .items(userResponseList)
                    .totalElements(users.getTotalElements())
                    .build();
        }

        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> userResponseList = users.stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<List<UserResponse>>builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .items(userResponseList)
                .totalElements(users.getTotalElements())
                .build();
    }
}
