package scw.common.transaction.exception;

public class TransactionEndException extends TransactionException{
	private static final long serialVersionUID = 1L;

	public TransactionEndException(Throwable e) {
		super(e);
	}
}
