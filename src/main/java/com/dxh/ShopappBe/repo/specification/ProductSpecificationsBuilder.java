package com.dxh.ShopappBe.repo.specification;

import com.dxh.ShopappBe.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.dxh.ShopappBe.repo.specification.SearchOperation.*;

public final  class ProductSpecificationsBuilder {

    public final List<SpecSearchCriteria> params;

    public ProductSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    // API
    public ProductSpecificationsBuilder with(final String key, final String operation, final String prefix, final Object value, final String suffix) {
        return with(null, key, operation, prefix, value, suffix);
    }

    public ProductSpecificationsBuilder with(final String orPredicate, final String key, final String operation, final String prefix, final Object value, final String suffix) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (searchOperation != null) {
            if (searchOperation == EQUALITY) { // the operation may be complex operation
                final boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    searchOperation = CONTAINS;
                } else if (startWithAsterisk) {
                    searchOperation = ENDS_WITH;
                } else if (endWithAsterisk) {
                    searchOperation = STARTS_WITH;
                }
            }
            params.add(new SpecSearchCriteria(orPredicate, key, searchOperation, value));
        }
        return this;
    }

    public Specification<Product> build() {
        if (params.isEmpty())
            return null;

        Specification<Product> result = new ProductSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new ProductSpecification(params.get(i)))
                    : Specification.where(result).and(new ProductSpecification(params.get(i)));
        }

        return result;
    }

    public ProductSpecificationsBuilder with(ProductSpecification spec) {
        params.add(spec.getCriteria());
        return this;
    }

    public ProductSpecificationsBuilder with(SpecSearchCriteria criteria) {
        params.add(criteria);
        return this;
    }
}