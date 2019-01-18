package scw.common.transaction.exception;

public class TransactionRollbackException extends TransactionException{
	private static final long serialVersionUID = 1L;

	public TransactionRollbackException(Throwable e){
		super(e);
	}
}
