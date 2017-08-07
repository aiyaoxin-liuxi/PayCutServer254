package com.dhb.entity.exception;

import java.util.List;
import java.util.Map;

public class ProxyBatchValidationException extends Exception{
	private static final long serialVersionUID = 1673297899078329110L;
	private List<Map<String,String>> errors;

	public ProxyBatchValidationException() {
		super();
	}

	public ProxyBatchValidationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		//super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProxyBatchValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProxyBatchValidationException(String message) {
		super(message);
	}
	public ProxyBatchValidationException(String message, List<Map<String,String>> errors,Throwable cause) {
		super(message, cause);
		this.errors = errors;
	}
	
	public ProxyBatchValidationException(String message,List<Map<String,String>> errors) {
		super(message);
		this.errors = errors;
	}

	public ProxyBatchValidationException(Throwable cause) {
		super(cause);
	}

	public List<Map<String, String>> getErrors() {
		return errors;
	}

	public void setErrors(List<Map<String, String>> errors) {
		this.errors = errors;
	}
	
}
