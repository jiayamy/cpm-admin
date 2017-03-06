package com.wondertek.cpm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.wondertek.cpm.web.filter.RequestInterceptor;

@Configuration
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

    @Override
	public void addInterceptors(InterceptorRegistry registry) {
    	registry.addInterceptor(new RequestInterceptor()).addPathPatterns("/**");
    	super.addInterceptors(registry);
	}
}
