package shuchaowen.core.exception;

public class BeansException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public BeansException(){
		super();
	}
	
	public BeansException(String message){
		super(message);
	}
	
	public BeansException(Throwable e){
		super(e);
	}
	
	public BeansException(String message, Throwable e){
		super(message, e);
	}
}
