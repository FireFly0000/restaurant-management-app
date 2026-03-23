package com.restaurant.businessservice.config;

import com.restaurant.commons.constant.Constant;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class I18nConfig {
    /**
     * Message source
     * @return MessageSource
     */
    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(Constant.SOURCE_MESSAGES_CLASSPATH,Constant.SOURCE_VALIDATIONS_CLASSPATH);
        messageSource.setDefaultEncoding(Constant.UTF_8);
        messageSource.setCacheSeconds(3600); // Reload messages every hour
        messageSource.setCacheSeconds(2);
        messageSource.setUseCodeAsDefaultMessage(true); // If msgSource don't see the key in i18n, then return the key.

        return messageSource;
    }

    /**
     * For API messages
     * @return LocaleContextResolver
     */
    @Bean
    public AcceptHeaderLocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.of(Constant.VN_LANGUAGE,Constant.VN_COUNTRY));

        return resolver;
    }

    /**
     * For validation messages
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator(){
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}

