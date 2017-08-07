package com.dhb.service;

import org.springframework.stereotype.Service;

import com.dhb.entity.ResponseMessage;
@Service
public class ErrorService {

	public ResponseMessage getErrorMessage(String message){
		ResponseMessage resp = new ResponseMessage();
		resp.setCode("ko");
		resp.setMessage(message);
		return resp;
	}
}
