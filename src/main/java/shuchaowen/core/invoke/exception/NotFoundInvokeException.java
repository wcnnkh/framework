package shuchaowen.core.invoke.exception;

public class NotFoundInvokeException extends RuntimeException{
	private static final long serialVersionUID = 6915230837396095833L;
	
	public NotFoundInvokeException(String name){
		super("not found invoke form name:" + name);
	}
}
