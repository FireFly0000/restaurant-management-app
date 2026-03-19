package com.restaurant.businessservice.common;

import com.restaurant.businessservice.constant.BusinessServiceConstant;
import com.restaurant.businessservice.core.service.business.dto.BusinessResponse;
import com.restaurant.businessservice.core.service.location.dto.BusinessLocationResponse;
import com.restaurant.businessservice.model.Business;
import com.restaurant.businessservice.model.Location;
import com.restaurant.commons.core.rpc.user.GetUsersByIdsResponse;
import com.restaurant.commons.core.rpc.user.UserRpcResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapperUtils {

    @SuppressWarnings("unchecked")
    public static BusinessResponse getBusinessResponse(Business business, Map<String, Object> argExtraData) {
        if (business == null) {
            return null;
        }

        List<BusinessLocationResponse> businessLocationResponses = null;

        if (argExtraData != null && argExtraData.containsKey(BusinessServiceConstant.EXT_BUSINESS_LOCATION)) {
            Object obj = argExtraData.get(BusinessServiceConstant.EXT_BUSINESS_LOCATION);
            if (obj instanceof List<?>) {
                businessLocationResponses = (List<BusinessLocationResponse>) obj;
            }
        }

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
                .averageRating(business.getAverageRating() != null ? business.getAverageRating() : 0.0) // Chống null
                .locations(businessLocationResponses)
                .deletedAt(business.getDeletedAt())
                .build();
    }

    public static BusinessLocationResponse getLocationResponse(Location location, Map<String, Object> argExtraData) {
        if (location == null) {
            return null;
        }

        BusinessLocationResponse.BusinessLocationResponseBuilder builder = BusinessLocationResponse.builder()
                .id(location.getId().toString())
                .businessId(location.getBusinessId().toString())
                .locationName(location.getBranchName())
                .address(location.getAddress())
                .phoneNumber(location.getPhoneNumber())
                .email(location.getEmail())
                .managerId(location.getManagerId() != null ? String.valueOf(location.getManagerId()) : null)
                .isActive(location.getIsActive())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .startDate(location.getStartDate());

        if (argExtraData != null && argExtraData.containsKey(BusinessServiceConstant.EXT_USERS)) {
            Object obj = argExtraData.get(BusinessServiceConstant.EXT_USERS);

            if (obj instanceof UserRpcResponse user) {
                builder.managerFirstName(user.getFirstName())
                        .managerLastName(user.getLastName());
            }
        }

        return builder.build();
    }
}
