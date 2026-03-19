package com.restaurant.businessservice.core.specification;

import com.restaurant.businessservice.core.service.business.dto.BusinessFilter;
import com.restaurant.businessservice.model.Business;
import com.restaurant.commons.utils.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BusinessSpecification {
    public static Specification<Business> filter(BusinessFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.hasText(filter.getSearch())){
                String pattern = "%"+filter.getSearch()+"%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern)
                ));
            }

            if(StringUtils.hasText(filter.getType())){
                predicates.add(cb.equal(root.get("businessType"), filter.getType()));
            }

            if(filter.getIsActived() == null || filter.getIsActived()){
                predicates.add(cb.equal(root.get("isActive"), true));
            }else{
                predicates.add(cb.equal(root.get("isActive"), false));
            }

            if(filter.getIsDeleted() == null || !filter.getIsDeleted()){
                predicates.add(cb.equal(root.get("deletedAt"), 0L));
            }else{
                predicates.add(cb.notEqual(root.get("deletedAt"), 0L));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
