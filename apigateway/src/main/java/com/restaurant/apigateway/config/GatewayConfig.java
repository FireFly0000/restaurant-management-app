package com.restaurant.apigateway.config;

import com.restaurant.commons.constant.Constant;
import com.restaurant.commons.utils.StringUtils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class GatewayConfig {
    @Bean
    public KeyResolver defaultKeyResolver(){
        return exchange -> {
          String userId = exchange.getRequest().getHeaders().getFirst(Constant.H_USER_ID);
          if(userId != null){
              return Mono.just(userId);
          }

          return Mono.just(this.getClientIp(exchange));
        };
    }

    private String getClientIp(ServerWebExchange exchange){
        String forwardedIp = exchange.getRequest().getHeaders().getFirst(Constant.H_FORWARDED_FOR);
        if(forwardedIp != null && !StringUtils.isEmpty(forwardedIp)){
            String realIp = forwardedIp.split(",")[0];
            if(realIp != null && !StringUtils.isEmpty(realIp)){
                return realIp.trim();
            }
        }

        return Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
    }
}
