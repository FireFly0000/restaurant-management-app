package com.restaurant.menuservice.core.context;

import com.restaurant.commons.core.UserContext;
import org.springframework.stereotype.Component;

@Component
public class RequestContext {

    private static final ThreadLocal<UserContext> CTX = new ThreadLocal<>();

    public static void set(String userId){
        CTX.set(new UserContext(userId));
    }

    public static UserContext get(){
        return CTX.get();
    }

    public static String userId() {
        return CTX.get().getUserId();
    }

    public static void clear(){
        CTX.remove();
    }
}