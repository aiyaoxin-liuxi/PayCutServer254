package com.dhb.entity.exception;

import java.util.List;
import java.util.Map;

public class ProxyAccountFileValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6978496775140018014L;
	private List<Map<String,String>> errors;


	public ProxyAccountFileValidationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProxyAccountFileValidationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		//super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ProxyAccountFileValidationException(String message, List<Map<String,String>> errors, Throwable cause) {
		super(message, cause);
		this.errors = errors;
		// TODO Auto-generated constructor stub
	}

	public ProxyAccountFileValidationException(String message,List<Map<String,String>> errors) {
		super(message);
		this.errors = errors;
		// TODO Auto-generated constructor stub
	}

	public ProxyAccountFileValidationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public List<Map<String, String>> getErrors() {
		return errors;
	}

	public void setErrors(List<Map<String, String>> errors) {
		this.errors = errors;
	}
	
}
