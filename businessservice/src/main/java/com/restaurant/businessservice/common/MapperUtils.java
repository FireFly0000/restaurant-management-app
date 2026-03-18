package com.restaurant.businessservice.common;

import com.restaurant.businessservice.constant.BusinessServiceConstant;
import com.restaurant.businessservice.core.service.business.dto.BusinessResponse;
import com.restaurant.businessservice.core.service.location.dto.BusinessLocationResponse;
import com.restaurant.businessservice.model.Business;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapperUtils {
    public static BusinessResponse getBusinessResponse(Business business, Map<String, Object> argExtraData){
        if(business != null){
            Set<BusinessLocationResponse> businessLocationResponses = argExtraData.get(BusinessServiceConstant.EXT_BUSINESS_LOCATION) != null ?
                    (Set<BusinessLocationResponse>) argExtraData.get(BusinessServiceConstant.EXT_BUSINESS_LOCATION) :
                    null;

            return BusinessResponse.builder()
                    .id(business.getId().toString())
                    .email(business.getEmail())
                    .name(business.getName())
                    .businessType(business.getBusinessType().name())
                    .description(business.getDescription())
                    .avatarUrl(business.getAvatarUrl())
                    .coverImgUrl(business.getCoverImgUrl())
                    .phoneNumber(business.getPhoneNumber())
                    .websiteUrl(business.getWebsiteUrl())
                    .isActive(business.getIsActive())
                    .createdAt(business.getCreatedAt())
                    .updatedAt(business.getUpdatedAt())
                    .locationsCount(business.getLocationsCount() != null ? business.getLocationsCount() : 0)
                    .ordersCount(business.getOrdersCount() != null ? business.getOrdersCount() : 0)
                    .averageRating(business.getAverageRating())
                    .locations(businessLocationResponses)
                    .build();

        }

        return null;
    }
}
