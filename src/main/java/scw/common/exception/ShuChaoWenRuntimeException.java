package scw.common.exception;

public class ShuChaoWenRuntimeException extends RuntimeException{
	private static final long serialVersionUID = -6858088801115238858L;
	
	public ShuChaoWenRuntimeException(){
		super();
	}
	
	public ShuChaoWenRuntimeException(String message){
		super(message);
	}
	
	public ShuChaoWenRuntimeException(Throwable e){
		super(e);
	}
	
	public ShuChaoWenRuntimeException(String message, Throwable e){
		super(message, e);
	}
}
