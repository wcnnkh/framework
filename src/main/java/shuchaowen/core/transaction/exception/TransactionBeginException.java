package shuchaowen.core.transaction.exception;

public class TransactionBeginException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public TransactionBeginException(){
		super();
	}
	
	public TransactionBeginException(Throwable e) {
		super(e);
	}
}
