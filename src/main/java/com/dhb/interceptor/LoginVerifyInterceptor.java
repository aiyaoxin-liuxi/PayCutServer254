package com.dhb.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dhb.controller.UserController;
import com.dhb.entity.BaseUser;
import com.dhb.service.QueueService;
import com.dhb.util.WebContextHolder;
import com.google.common.base.Strings;



public class LoginVerifyInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = Logger.getLogger(LoginVerifyInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	
    	String requestURI = request.getRequestURI();
		String context = request.getContextPath();
		requestURI = requestURI.replace(context,"");
		if(requestURI.contains("css/")){
			return super.preHandle(request, response, handler);
		}
		if(requestURI.contains("js/")){
			return super.preHandle(request, response, handler);
		}
		if(requestURI.contains("common/")){
			return super.preHandle(request, response, handler);
		}
		
		if("/template".equals(requestURI)){
			return super.preHandle(request, response, handler);
		}
		HttpSession session=request.getSession();
		WebContextHolder.setSession(session);
		String currentToken = WebContextHolder.getCurrentToken();
		BaseUser user=WebContextHolder.getCurrentUser();
		session.removeAttribute("otherPlaceLogin");
		if("/logout".equals(requestURI)){
			return super.preHandle(request, response, handler);
		}
    	if(user!=null){
    		
    			String onlyToken=UserController.map.get(user.getUserName());
    			if(!Strings.isNullOrEmpty(onlyToken)){
    				if(!onlyToken.equals(currentToken)){
    					session.setAttribute("otherPlaceLogin", "有人在其他地方登陆,请退出");
    					request.getRequestDispatcher("/welcome.jsp").forward(request, response);
    					return false;
    				}
    			}
    		
    		return super.preHandle(request, response, handler);
    	}else{
    		if(requestURI.contains("/proxySendSalary")){
    			return super.preHandle(request, response, handler);
    		}
    		if(requestURI.contains("/proxyPayBatch")){
    			return super.preHandle(request, response, handler);
    		}
    		if(requestURI.contains("/dhb")){
    			return super.preHandle(request, response, handler);
    		}
    		String userName = (String) request.getParameter("userName");
    		String password = (String) request.getParameter("password");

    	
    		if(Strings.isNullOrEmpty(userName)||Strings.isNullOrEmpty(password)){
    			request.getRequestDispatcher("/login.jsp").forward(request, response);
				return false;
  
    		}
    		
    		  
    		return super.preHandle(request, response, handler);
    		
    	}
    }
    
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		try{
			
			super.postHandle(request, response, handler, modelAndView);
		}catch(Exception e){
			throw e;
		}finally{
			WebContextHolder.removeSession();
		}
	}



}
