package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.OrderCreateRequest;
import com.dxh.ShopappBe.dto.response.OrderResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.ProductResponse;
import com.dxh.ShopappBe.entity.*;
import com.dxh.ShopappBe.enums.OrderStatus;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.OrderMapper;
import com.dxh.ShopappBe.repo.*;
import com.dxh.ShopappBe.service.WebSocketService;
import com.dxh.ShopappBe.service.interfac.OrderService;
import com.dxh.ShopappBe.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dxh.ShopappBe.utils.AppConstant.SORT_BY;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    UserRepository userRepository;
    CartRepository cartRepository;
    AddressRepository addressRepository;
    DiscountRepository discountRepository;
    CartItemRepository cartItemRepository;
    OrderMapper orderMapper;
    ProductRepository productRepository;
    WebSocketService webSocketService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderCreateRequest orderCreateRequest) {
        // kiểm tra user đang login
        User user= checkUser();

        // 1. Lấy Cart của user
        Cart cart = cartRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // 2. Lấy Address
        Address address = addressRepository.findByIdAndUser_Id(orderCreateRequest.getAddressId(),user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_EXITSTED));

        // 3. Lấy Discount nếu có
        Discount discount = null;
        if (orderCreateRequest.getDiscountId() != null) {
            discount = discountRepository.findById(orderCreateRequest.getDiscountId())
                    .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_EXISTED));
        }

        double total = cart.getTotal();

        // 4. Áp dụng discount nếu có
        if (discount != null) {
            if(discount.getQuantity()<=0 && discount.getIsActive()){
                throw new AppException(ErrorCode.DISCOUNT_EXPIRED);
            }
            total = total * (100 - discount.getDiscountPercent()) / 100;
        }

        // 5. Tạo Order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(address)
                .discount(discount)
                .status(OrderStatus.PENDING_CONFIRMATION)
                .totalPrice(total)
//                .orderItems(new ArrayList<>()) // tạm thời rỗng
                .isPaid(false)
//                .vnpTxnRef(UUID.randomUUID().toString()) // hoặc để null rồi update sau
                .build();

        // 6. tạo OrderItem
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            //set lại số lượng
            product.setQuantity(product.getQuantity()-cartItem.getQuantity());
            product.setTotalSold(product.getTotalSold()+cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .amount(cartItem.getAmount())
                    .order(order)
                    .build();
            orderItems.add(orderItem);
        }

        // 7. Lưu Order và OrderItems (Cascade ALL)
        order.setOrderItems(orderItems);
        OrderResponse orderResponse = orderMapper.toOrderResponse(orderRepository.save(order)) ;
        orderResponse.setOrderStatus(order.getStatus().name());

        // 8. Xoá giỏ hàng (hoặc chỉ xoá CartItem), giảm số discount
        cart.getItems().clear();
        cart.setTotal(0.0);
        cartRepository.save(cart);

        if(discount!=null){
            discount.setQuantity(discount.getQuantity()-1);
            discountRepository.save(discount);
        }

        webSocketService.sendNewOrderNotification(order, user);

        // 9. Trả về kết quả
        return orderResponse;
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        orderResponse.setOrderStatus(order.getStatus().name());
        return orderResponse;
    }

    @Override
    public PageResponse<List<OrderResponse>> getAllOrder(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = Utils.createPageable(pageNo, pageSize, sortBy);
        Page<Order> orders = orderRepository.findAll(pageable);
        List<OrderResponse> response = orders.stream().map(o -> {
            OrderResponse orderResponse = orderMapper.toOrderResponse(o);
            orderResponse.setOrderStatus(o.getStatus().name());
            return orderResponse;
        }).toList();
        return PageResponse.<List<OrderResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(orders.getTotalPages())
                .items(response)
                .totalElements(orders.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<List<OrderResponse>> getOrderByStatus(String status, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = Utils.createPageable(pageNo, pageSize, sortBy);
        Page<Order> orders = orderRepository.findAllByStatus(OrderStatus.valueOf(status.toUpperCase()),pageable);
        List<OrderResponse> response = orders.stream().map(o -> {
            OrderResponse orderResponse = orderMapper.toOrderResponse(o);
            orderResponse.setOrderStatus(o.getStatus().name());
            return orderResponse;
        }).toList();
        return PageResponse.<List<OrderResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(orders.getTotalPages())
                .items(response)
                .totalElements(orders.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<List<OrderResponse>> getMyOrder(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = Utils.createPageable(pageNo, pageSize, sortBy);
        User user = checkUser();

        Page<Order> orders = orderRepository.findAllByUser_Id(user.getId(),pageable);
        List<OrderResponse> response = orders.stream().map(o -> {
            OrderResponse orderResponse = orderMapper.toOrderResponse(o);
            orderResponse.setOrderStatus(o.getStatus().name());
            return orderResponse;
        }).toList();
        return PageResponse.<List<OrderResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(orders.getTotalPages())
                .items(response)
                .totalElements(orders.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<List<OrderResponse>> getOrderByUserId(Long id, Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = Utils.createPageable(pageNo, pageSize, sortBy);
        Page<Order> orders = orderRepository.findAllByUser_Id(id,pageable);
        List<OrderResponse> response = orders.stream().map(o -> {
            OrderResponse orderResponse = orderMapper.toOrderResponse(o);
            orderResponse.setOrderStatus(o.getStatus().name());
            return orderResponse;
        }).toList();
        return PageResponse.<List<OrderResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(orders.getTotalPages())
                .items(response)
                .totalElements(orders.getTotalElements())
                .build();
    }


    private User checkUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED) {
            order.setIsPaid(true);
        }

        Order savedOrder = orderRepository.save(order);

        // Gửi notification tới customer
        webSocketService.sendOrderStatusNotification(savedOrder, savedOrder.getUser());

        OrderResponse orderResponse = orderMapper.toOrderResponse(savedOrder);
        orderResponse.setOrderStatus(savedOrder.getStatus().name());
        return orderResponse;
    }
}
