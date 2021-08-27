package io.basc.framework.security.authority;

public class AuthorizationFailureException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public AuthorizationFailureException(String msg){
		super(msg);
	}
}
