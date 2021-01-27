package scw.instance;

public class InstanceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InstanceException(String message){
		super(message);
	}
	
	public InstanceException(Throwable e){
		super(e);
	}
	
	public InstanceException(String message, Throwable e){
		super(message, e);
	}
}
