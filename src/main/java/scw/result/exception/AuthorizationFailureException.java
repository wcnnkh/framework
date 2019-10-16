package scw.result.exception;

public class AuthorizationFailureException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public AuthorizationFailureException(String msg){
		super(msg);
	}
}
