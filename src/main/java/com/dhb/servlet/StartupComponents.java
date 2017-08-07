package com.dhb.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.dhb.util.SpringContextHelper;

/**
 * Servlet implementation class StartupComponents
 */
public class StartupComponents extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

	public StartupComponents() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init();
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(config.getServletContext());
		SpringContextHelper.getInstance().init(ctx);
		
	
	}

	@Override
	public void destroy() {
		super.destroy();
	}


	

	
	

}
