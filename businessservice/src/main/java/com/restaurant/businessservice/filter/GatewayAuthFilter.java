package com.restaurant.businessservice.filter;

import com.restaurant.businessservice.core.context.RequestContext;
import com.restaurant.commons.constant.Constant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static boolean isPublicRequest(HttpServletRequest request){
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        return Constant.PUBLIC_ENDPOINT.stream()
                .anyMatch(endpoint ->
                        pathMatcher.match(endpoint.getLeft(), requestPath) &&
                        endpoint.getRight().equalsIgnoreCase(requestMethod)
                );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            if(!isPublicRequest(request)){
                String userId = request.getHeader(Constant.H_USER_ID);

                RequestContext.set(userId);
            }
            filterChain.doFilter(request, response);
        }finally {
            RequestContext.clear();
        }
    }
}
