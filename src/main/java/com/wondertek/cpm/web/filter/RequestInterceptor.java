package com.wondertek.cpm.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.wondertek.cpm.security.SecurityUtils;

public class RequestInterceptor extends HandlerInterceptorAdapter {
	private final Logger log = LoggerFactory.getLogger(RequestInterceptor.class);
	private NamedThreadLocal<Long>  startTimeThreadLocal = new NamedThreadLocal<Long>("RequestInterceptor-StartTime");  

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
        startTimeThreadLocal.set(System.currentTimeMillis());//线程绑定变量（该数据只有当前请求的线程可见）  
        return true;//继续流程  
	}
	
	@Override
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	    long executionTime = System.currentTimeMillis() - startTimeThreadLocal.get();
	    String userName = SecurityUtils.getCurrentUserLogin();
		StringBuilder message = new StringBuilder();
		message.append("Login User [").append(userName)
			.append("]. From IP [").append(request.getRemoteAddr())
			.append("]. Request Url [").append(request.getMethod()).append(" ").append(request.getRequestURL())
			.append("]. Took [").append(executionTime)
			.append("] ms. Request params [")
			.append(request.getQueryString())
			.append("].");
		log.info(message.toString());
	}
}