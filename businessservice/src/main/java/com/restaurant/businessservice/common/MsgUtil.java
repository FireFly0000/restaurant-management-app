package com.restaurant.businessservice.common;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MsgUtil {
    private final MessageSource _msgSource;

    public MsgUtil(MessageSource _msgSource) {
        this._msgSource = _msgSource;
    }

    public String getMessage(String key){
        return _msgSource.getMessage(key, null,LocaleContextHolder.getLocale());
    }

    public String getMessage(String key, Object... args){
        return _msgSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
