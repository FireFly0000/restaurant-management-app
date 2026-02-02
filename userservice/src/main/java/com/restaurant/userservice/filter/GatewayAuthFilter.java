package com.restaurant.userservice.filter;

import com.restaurant.userservice.core.context.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Viết hàm check request isPublic


        // Check nullUserId
        String userId = request.getHeader("X-User-Id");

        try{
            RequestContext.set(userId);
            filterChain.doFilter(request, response);
        }finally {
            RequestContext.clear();
        }
    }
}
