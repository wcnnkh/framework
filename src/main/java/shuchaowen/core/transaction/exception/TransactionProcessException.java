package shuchaowen.core.transaction.exception;

public class TransactionProcessException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public TransactionProcessException(Throwable e) {
		super(e);
	}
}
