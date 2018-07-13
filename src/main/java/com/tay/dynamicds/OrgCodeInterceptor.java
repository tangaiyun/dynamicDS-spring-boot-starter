package com.tay.dynamicds;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class OrgCodeInterceptor implements HandlerInterceptor{
	private static final Logger LOGGER = LoggerFactory.getLogger(HandlerInterceptor.class);
	private String orgCodeHeaderName = "orgCode";
	
	private Set<String> validOrgCodes;
	
	
	
	public void setOrgCodeHeaderName(String orgCodeName) {
		orgCodeHeaderName = orgCodeName;
	}
	
	public void setValidOrgCodes(Set<String> validOrgCodes) {
		this.validOrgCodes = validOrgCodes;
	}
	
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        System.out.printf("preHandle被调用");
        String orgCodeVal = httpServletRequest.getHeader(orgCodeHeaderName);
        if(orgCodeVal == null) {
        	LOGGER.error("The request without a header named as " + orgCodeHeaderName);
        	return false;
        }
        if(!validOrgCodes.contains(orgCodeVal)) {
        	LOGGER.error(String.format(" the orgCode %s is not valid.", orgCodeVal));
        	return false;
        }
        OrgCodeHolder.putOrgCode(httpServletRequest.getHeader(orgCodeHeaderName));
        return true;    
    }
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle被调用");
        OrgCodeHolder.remove();
    }
	
	
}
