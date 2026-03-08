package com.restaurant.authservice.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Utils {
    private static MessageSource _messageSource = null;

    public Utils(
            MessageSource _msgSource
    ){
        this._messageSource = _msgSource;
    }


    public static String getMessage(String code, Object... parameters){
        return  _messageSource.getMessage(code, parameters, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String code){
        return  _messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
