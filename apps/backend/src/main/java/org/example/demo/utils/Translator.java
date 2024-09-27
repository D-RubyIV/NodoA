package org.example.demo.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by Trong Phu on 29/08/2024 22:12
 *
 * @author Trong Phu
 */
@Component
public class Translator {
    @Autowired
    private MessageSource messageSource;

    public String toLocale(String msgCode){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, null, locale);
    }

    public String toLocale(String msgCode, Object[] objects){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, objects, locale);
    }

}