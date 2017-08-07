package com.dhb.entity;

public class CommonObject<T> {

	private Class<T> genericType;
	
    private Object object;

	public CommonObject(Object object){
		this.object = object;
		this.genericType =(Class<T>) object.getClass();
	}
	public Class<T> getGenericType(){
		return genericType;
	}
	public Object getContent(){
		return object;
	}
}
