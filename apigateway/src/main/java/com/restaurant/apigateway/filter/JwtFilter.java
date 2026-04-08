package com.restaurant.apigateway.filter;

import com.restaurant.apigateway.core.service.jwt.IJwtService;
import com.restaurant.commons.constant.Constant;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtFilter implements GlobalFilter, Ordered {

    private final IJwtService _jwtService;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtFilter(IJwtService jwtService){
        this._jwtService = jwtService;
    }

    /**
     *
     * @param request ServerHttpRequest
     * @return boolean
     */
    private static boolean isPublicRequest(ServerHttpRequest request){
        String requestPath = request.getPath().pathWithinApplication().value();
        String requestMethod = request.getMethod().name();

        return Constant.PUBLIC_ENDPOINT.stream()
                .anyMatch(endpoint ->
                        pathMatcher.match(endpoint.getLeft(), requestPath) &&
                        endpoint.getRight().equalsIgnoreCase(requestMethod)
                );
    }

    /**
     *
     * @param exchange ServerWebExchange
     * @return Mono<Void>
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
        {
          "status": 401,
          "error": "Unauthorized",
          "message": "Missing or invalid Authorization header"
        }
        """;

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Filter implementation
     * @param exchange ServerWebExchange
     * @param chain GatewayFilterChain
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(isPublicRequest(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        System.out.println("MY AUTH TOKEN == " + auth);

        if(auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = auth.substring(7);

        if(!_jwtService.isValidToken(token)){
            return unauthorized(exchange);
        }

        String userId = _jwtService.extractSubject(token);

        System.out.println("MY USER ID" + userId);

        ServerHttpRequest newReq = exchange.getRequest().mutate()
                .header(Constant.H_USER_ID, userId)
                .header(Constant.H_ACCESS_TOKEN, token)
                //.headers(h -> h.remove(HttpHeaders.AUTHORIZATION))
                .build();

        return chain.filter(exchange.mutate().request(newReq).build());
    }

    /**
     *
     * @return int
     */
    @Override
    public int getOrder() {
        return -999999;
    }

    public static class Config {
        // Configuration properties if needed
    }
}
