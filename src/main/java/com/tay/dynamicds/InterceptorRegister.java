package com.tay.dynamicds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class InterceptorRegister implements WebMvcConfigurer{
	@Autowired
	private OrgCodeInterceptor orgCodeInterceptor;
	
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orgCodeInterceptor);
    }
	
}
