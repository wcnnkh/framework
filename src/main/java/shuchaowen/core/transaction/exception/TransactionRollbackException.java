package shuchaowen.core.transaction.exception;

public class TransactionRollbackException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public TransactionRollbackException(Throwable e){
		super(e);
	}
}
