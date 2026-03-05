package com.restaurant.businessservice.common;

import com.restaurant.businessservice.core.service.business.dto.BusinessResponse;
import com.restaurant.businessservice.model.Business;

import java.util.Map;

public class MapperUtils {
    public static BusinessResponse getBusinessResponse(Business business, Map<String, Object> argExtraData){
        if(business != null){
            BusinessResponse response = BusinessResponse.builder()
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
                    .locationCount(argExtraData.get("location_count") != null ? Integer.parseInt(argExtraData.get("location_count").toString()) : 0)
                    .build();
        }

        return null;
    }
}
