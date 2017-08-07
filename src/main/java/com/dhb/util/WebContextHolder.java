package com.dhb.util;

import javax.servlet.http.HttpSession;

import com.dhb.controller.UserController;
import com.dhb.entity.BaseUser;

public class WebContextHolder {
	public final static String currentUser ="MYCURRENTUSERR";
	public final static String currentToken ="MYCURRENTTOKEN";
	private static ThreadLocal<HttpSession> sessionHold = new ThreadLocal<HttpSession>();
	
	public static HttpSession getCurrentSession(){
		return sessionHold.get();
	}
	
	public static void setSession(HttpSession session){
		 sessionHold.set(session);;
	}

	public static BaseUser getCurrentUser(){
		HttpSession session=getCurrentSession();
		BaseUser user =(BaseUser)session.getAttribute(currentUser);
		return user;
	}
	public static String getCurrentToken(){
		HttpSession session=getCurrentSession();
		return (String)session.getAttribute(currentToken);
	}

     public static String getWebRelativePath(){
		
		return getCurrentSession().getServletContext().getContextPath();
				}
	public static void removeSession(){
	
		sessionHold.remove();
	}
	public static void removeUserSession(){
		HttpSession session=getCurrentSession();
		if(session!=null){
			BaseUser user = getCurrentUser();
			if(user!=null){
				UserController.map.remove(user.getUserName());
			}
			session.removeAttribute(currentToken);
			session.removeAttribute(currentUser);
		}
		
		
	}
}
