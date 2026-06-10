package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.ForgotPasswordRequest;
import com.dxh.ShopappBe.dto.request.ResetPasswordRequest;
import com.dxh.ShopappBe.dto.request.UserCreationRequest;
import com.dxh.ShopappBe.dto.request.UserUpdateRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.UserResponse;
import com.dxh.ShopappBe.dto.response.UserUpdateResponse;
import com.dxh.ShopappBe.entity.Cart;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.entity.VerificationToken;
import com.dxh.ShopappBe.enums.Role;
import com.dxh.ShopappBe.enums.Gender;
import com.dxh.ShopappBe.enums.VerifyType;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.UserMapper;
import com.dxh.ShopappBe.repo.*;
import com.dxh.ShopappBe.repo.specification.UserSpecificationsBuilder;
import com.dxh.ShopappBe.service.AwsS3Service;
import com.dxh.ShopappBe.service.EmailService;
import com.dxh.ShopappBe.service.interfac.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
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
    EmailService emailService;
    VerificationTokenRepository vrRepository;
    AwsS3Service awsS3Service;

    static Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username", "email", "phoneNumber", "name");


    @Transactional
    @Override
    public UserResponse createUser(UserCreationRequest request){
        log.info("Creating user");
        //        kiểm tra email
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (!user.getEnabled()) {
                userRepository.delete(user); // Xoá user chưa xác minh
            } else {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
        });

//        kiểm tra username
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

//k         kiểm tra sdt
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber()))
            throw new AppException(ErrorCode.PHONE_EXISTED);

//        lưu tạm thời enabled=false
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        user.setRoles(Set.of(
                roleRepository.findByName(Role.USER.name())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED))
        ));
        User savedUser = userRepository.save(user);

//        gửi mail verify
        String secretCode = UUID.randomUUID().toString();
        vrRepository.save(VerificationToken.builder()
                        .secretKey(secretCode)
                        .user(user)
                        .verifyType(VerifyType.REGISTER)
                        .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build());
        try {
            emailService.sendVerificationEmail(request.getEmail(),request.getFullName(),secretCode);
        }catch (IOException e){
            throw new AppException(ErrorCode.SEND_FAILED);
        }
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

    @Override
    @Transactional
    public void verifyRegister(String secretKey) {
        VerificationToken vt = vrRepository.findBySecretKeyAndVerifyType(secretKey, VerifyType.REGISTER)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFY_KEY));

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VERIFY_KEY_EXPIRED);
        }

        User user = vt.getUser();
        if (user.getEnabled()) {
            throw new AppException(ErrorCode.ALREADY_VERIFIED);
        }

        user.setEnabled(true);
        userRepository.save(user);

        // Xoá token để tránh dùng lại
        vrRepository.delete(vt);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .filter(User::getEnabled)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xoá token cũ (nếu cần)
        vrRepository.deleteByUserAndVerifyType(user, VerifyType.RESET_PASSWORD);

        // Tạo token mới
        String resetCode = generateAlphanumericCode(6);
        vrRepository.save(VerificationToken.builder()
                .secretKey(resetCode)
                .user(user)
                .verifyType(VerifyType.RESET_PASSWORD)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build());

        // Gửi email
        try {
            emailService.sendResetPasswordEmail(user.getEmail(), user.getFullName(), resetCode);
        }catch (IOException e){
            throw new AppException(ErrorCode.SEND_FAILED);
        }
    }

    @Transactional
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        VerificationToken vt = vrRepository.findBySecretKeyAndVerifyType(request.getResetCode(), VerifyType.RESET_PASSWORD)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFY_KEY));

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VERIFY_KEY_EXPIRED);
        }

        User user = vt.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        vrRepository.delete(vt); // Xoá token sau khi dùng
    }

    @Override
    public UserUpdateResponse updateMyUser(UserUpdateRequest request, MultipartFile userImage) {
        User user = checkUser();
        if(userImage != null && !userImage.isEmpty()){
            user.setAvatar(awsS3Service.saveImageToS3(userImage));
        }
        user.setFullName(request.getFullName());
        user.setDob(request.getDob());
        if (request.getGender() != null && !request.getGender().isBlank()) {
            user.setGender(Gender.valueOf(request.getGender()));
        }
        return userMapper.toUserUpdateResponse(userRepository.save(user));
    }


    private String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom(); // an toàn hơn Random thường

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    private User checkUser(){
        return userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    public UserResponse getMyInfo() {
        return userMapper.toUserResponse(checkUser());
    }
}
