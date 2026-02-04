package com.restaurant.apigateway.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("")
    public Mono<?> defaultFallback(){
        return Mono.just("Hệ thống bị mất kết nối. Vui lòng thử lại sau.");
    }
}
