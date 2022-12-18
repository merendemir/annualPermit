package com.module.annual.permit.exceptions;

import lombok.Getter;

@Getter
public class DataNotFoundException extends RuntimeException{

	private String[] params;

	public DataNotFoundException(String msg){
		super(msg);
	}

	public DataNotFoundException(String msg, String... params) {
		super(msg);
		this.params = params;
	}
}
