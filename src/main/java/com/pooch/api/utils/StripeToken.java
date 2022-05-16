package com.pooch.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class StripeToken {
	public static final String VALID = "valid";
	public static final String INVALID = "invalid";
	
	private String token;
	private String paymentMethodId;
//	private String status;
//	private String msg;
	
//	public StripeToken(String token) {
//		this(token,null);
//	}
//	
//	public StripeToken(String token, String status) {
//		this(token,status,null);
//	}
//	
//	public StripeToken(String token, String status, String msg) {
//		this.token = token;
//		this.status = status;
//		this.msg = msg;
//	}
	
	
}
