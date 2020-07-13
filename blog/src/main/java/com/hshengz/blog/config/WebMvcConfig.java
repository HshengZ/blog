package com.hshengz.blog.config;

import com.hshengz.blog.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /*@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }*/

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/admin/**")//要拦截的路径
                .excludePathPatterns("/admin/login")//放行的路径
                .excludePathPatterns("/admin")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/templates/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/lib/**");
    }
}
