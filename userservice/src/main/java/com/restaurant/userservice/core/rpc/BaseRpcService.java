package com.restaurant.userservice.core.rpc;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;

import java.util.function.Supplier;

@Slf4j
public abstract class BaseRpcService {

    protected <T> T execute(Supplier<T> action, String actionName){
        long startTime = System.currentTimeMillis();
        try{
            T result = action.get();
            log.info("RPC Action [{}] completed in {}ms", actionName, System.currentTimeMillis() - startTime);

            return result;
        }catch (IllegalArgumentException ex){
            log.warn("RPC Action [{}] invalid argument: {}", actionName, ex.getMessage());
            throw new RpcException(RpcException.BIZ_EXCEPTION, "Invalid Argument: " + ex.getMessage());
        }catch (Exception ex){
            log.error("RPC Action [{}] failed", actionName, ex);
            throw new RpcException(RpcException.UNKNOWN_EXCEPTION, "Internal Server Error");
        }
    }
}
