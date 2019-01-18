package scw.common.transaction.exception;

public class TransactionBeginException extends TransactionException{
	private static final long serialVersionUID = 1L;
	
	public TransactionBeginException(){
		super();
	}
	
	public TransactionBeginException(Throwable e) {
		super(e);
	}
}
