package com.dhb.entity;

public class ResponseResult {
	
	private String message;
	
	private String param;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	@Override
	public String toString() {
		return "ResponseResult [message=" + message + ", param=" + param + "]";
	}

}
