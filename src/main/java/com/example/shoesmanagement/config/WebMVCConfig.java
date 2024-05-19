package com.example.shoesmanagement.config;

import com.example.shoesmanagement.interceptor.ManageLoginInterceptor;
import com.example.shoesmanagement.interceptor.StaffLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    private StaffLoginInterceptor staffLoginInterceptor;

    @Autowired
    private ManageLoginInterceptor manageLoginInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(manageLoginInterceptor)
                .addPathPatterns("/manage/**")
                .excludePathPatterns("/manage/login");
        registry.addInterceptor(staffLoginInterceptor)
                .addPathPatterns("/manage/hang")
                .excludePathPatterns("/manage/login");
    }

    @Bean("messageSource")
    public MessageSource loadMessageSource(){
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();

        source.setBasenames(
                "classpath:/message/valiate",
                "classpath:/i18n/home"
        );
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return source;
    }

    @Bean("localeResolver")
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setCookieMaxAge(60);
        cookieLocaleResolver.setCookiePath("/");
        cookieLocaleResolver.setDefaultLocale(new Locale("en"));
        return cookieLocaleResolver;
    }
}
