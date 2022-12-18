package com.module.annual.permit.exceptions;

import lombok.Getter;

@Getter
public class DataNotAcceptableException extends RuntimeException {

	private String[] params;
	public DataNotAcceptableException(String msg){
		super(msg);
	}

	public DataNotAcceptableException(String msg, String... params) {
		super(msg);
		this.params = params;
	}
}
