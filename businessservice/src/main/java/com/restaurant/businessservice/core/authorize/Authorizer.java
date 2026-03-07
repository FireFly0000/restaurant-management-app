package com.restaurant.businessservice.core.authorize;

import com.restaurant.businessservice.core.context.RequestContext;
import com.restaurant.businessservice.core.service.business.IBusinessService;
import com.restaurant.commons.constant.Entity;
import com.restaurant.commons.core.UserContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("_authorizer")
public class Authorizer {
    private final IBusinessService _businessService;

    public Authorizer(IBusinessService _businessService) {
        this._businessService = _businessService;
    }

    public boolean isOwner(UUID id, String entity) {
        if(RequestContext.get() == null) return false;
        UserContext cxt = RequestContext.get();
        switch (entity) {
            case Entity.BUSINESS -> {
                return this._businessService.existByIdAndOwnerId(id, UUID.fromString(cxt.getUserId()));
            }
            case Entity.LOCATION -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isLogedIn() {
        return RequestContext.get().getUserId() != null;
    }
}
