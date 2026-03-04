package com.restaurant.commons.utils;

import java.util.concurrent.CompletableFuture;

public class AppUtils {
    public static <T> T getTaskResult(CompletableFuture<T> task){
        try{
            return (task.isDone() && !task.isCompletedExceptionally()) ? task.get() : null;
        }catch (Exception ex){
            return null;
        }
    }
}
