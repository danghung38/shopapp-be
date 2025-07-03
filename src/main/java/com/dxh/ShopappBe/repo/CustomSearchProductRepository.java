package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.ProductResponse;
import com.dxh.ShopappBe.entity.Category;
import com.dxh.ShopappBe.entity.Product;
import com.dxh.ShopappBe.mapper.ProductMapper;
import com.dxh.ShopappBe.repo.specification.SpecSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dxh.ShopappBe.utils.AppConstant.SEARCH_SPEC_OPERATOR;
import static com.dxh.ShopappBe.utils.AppConstant.SORT_BY;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSearchProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private final ProductMapper productMapper;

    /**
     * Search product join category
     *
     *
     * @param product
     * @param category
     * @return
     */
    public PageResponse<List<ProductResponse>> searchProductByCriteriaWithJoin(Pageable pageable, String[] product, String[] category) {
        log.info("-------------- search product multiple and join --------------");

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = builder.createQuery(Product.class);
        Root<Product> productRoot = query.from(Product.class);
        Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);

        Predicate productPre = null;
        Predicate categoryPre = null;

// ----- Xử lý điều kiện product -----
        List<Predicate> productPreList = new ArrayList<>();
        if (product != null && product.length > 0) {
            for (String p : product) {
                Matcher matcher = pattern.matcher(p);
                if (matcher.find()) {
                    SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                            matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)
                    );
                    productPreList.add(toProductPredicate(productRoot, builder, searchCriteria));
                }
            }
            if (!productPreList.isEmpty()) {
                // có thể đổi chỗ này thành OR nếu muốn:
                productPre = builder.and(productPreList.toArray(new Predicate[0]));
            }
        }

// ----- Xử lý điều kiện category -----
        List<Predicate> categoryPreList = new ArrayList<>();
        Join<Category, Product> categoryJoin = null;
        if (category != null && category.length > 0) {
            categoryJoin = productRoot.join("category", JoinType.INNER);
            for (String c : category) {
                Matcher matcher = pattern.matcher(c);
                if (matcher.find()) {
                    SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                            matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)
                    );
                    categoryPreList.add(toCategoryPredicate(categoryJoin, builder, searchCriteria));
                }
            }
            if (!categoryPreList.isEmpty()) {
                // có thể đổi chỗ này thành OR nếu muốn:
                categoryPre = builder.and(categoryPreList.toArray(new Predicate[0]));
            }
        }

// ----- Gộp điều kiện lại -----
        Predicate finalPre = null;
        if (productPre != null && categoryPre != null) {
            // 👉 Đây là chỗ bạn quyết định AND hay OR giữa 2 nhóm
            finalPre = builder.and(productPre, categoryPre);
        } else if (productPre != null) {
            finalPre = productPre;
        } else if (categoryPre != null) {
            finalPre = categoryPre;
        }

// Áp where nếu có
        if (finalPre != null) {
            query.where(finalPre);
        }

        // ====== Áp dụng sắp xếp (order by) từ pageable ======
        List<Order> orderList = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            Path<Object> path = productRoot.get(order.getProperty());
            orderList.add(order.isAscending() ? builder.asc(path) : builder.desc(path));
        }
        if (!orderList.isEmpty()) {
            query.orderBy(orderList);
        }

        List<Product> products = entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
        List<ProductResponse> productResponseList = products.stream().map(productMapper::toProductResponse).toList();

        long count = countProductJoinCategory(product, category);

        return PageResponse.<List<ProductResponse>>builder()
                .pageNo(pageable.getPageNumber()+1)
                .pageSize(pageable.getPageSize())
                .totalPage((int) Math.ceil((double) count / pageable.getPageSize()))
                .totalElements(count)
                .items(productResponseList)
                .build();
    }

    private Predicate toProductPredicate(Root<Product> root, CriteriaBuilder builder, SpecSearchCriteria criteria) {
        log.info("-------------- toProductPredicate --------------");
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    private Predicate toCategoryPredicate(Join<Category, Product> root, CriteriaBuilder builder, SpecSearchCriteria criteria) {
        log.info("-------------- toCategoryPredicate --------------");
        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
        };
    }

    /**
     * Count product by conditions
     *
     * @param product
     * @param category
     * @return
     */
    private long countProductJoinCategory(String[] product, String[] category) {
        log.info("-------------- countProductJoinCategory --------------");

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Product> productRoot = query.from(Product.class);
        Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);

        Predicate productPre = null;
        Predicate categoryPre = null;

// ----- Xử lý điều kiện product -----
        List<Predicate> productPreList = new ArrayList<>();
        if (product != null && product.length > 0) {
            for (String p : product) {
                Matcher matcher = pattern.matcher(p);
                if (matcher.find()) {
                    SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                            matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)
                    );
                    productPreList.add(toProductPredicate(productRoot, builder, searchCriteria));
                }
            }
            if (!productPreList.isEmpty()) {
                // có thể đổi chỗ này thành OR nếu muốn:
                productPre = builder.and(productPreList.toArray(new Predicate[0]));
            }
        }

// ----- Xử lý điều kiện category -----
        List<Predicate> categoryPreList = new ArrayList<>();
        Join<Category, Product> categoryJoin = null;
        if (category != null && category.length > 0) {
            categoryJoin = productRoot.join("category", JoinType.INNER);
            for (String c : category) {
                Matcher matcher = pattern.matcher(c);
                if (matcher.find()) {
                    SpecSearchCriteria searchCriteria = new SpecSearchCriteria(
                            matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)
                    );
                    categoryPreList.add(toCategoryPredicate(categoryJoin, builder, searchCriteria));
                }
            }
            if (!categoryPreList.isEmpty()) {
                // có thể đổi chỗ này thành OR nếu muốn:
                categoryPre = builder.and(categoryPreList.toArray(new Predicate[0]));
            }
        }

// ----- Gộp điều kiện lại -----
        Predicate finalPre = null;
        if (productPre != null && categoryPre != null) {
            // 👉 Đây là chỗ bạn quyết định AND hay OR giữa 2 nhóm
            finalPre = builder.and(productPre, categoryPre);
        } else if (productPre != null) {
            finalPre = productPre;
        } else if (categoryPre != null) {
            finalPre = categoryPre;
        }

        query.select(builder.count(productRoot));
// Áp where nếu có
        if (finalPre != null) {
            query.where(finalPre);
        }



        return entityManager.createQuery(query).getSingleResult();
    }
}
