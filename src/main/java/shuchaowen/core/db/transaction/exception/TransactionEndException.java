package shuchaowen.core.db.transaction.exception;

public class TransactionEndException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public TransactionEndException(Throwable e) {
		super(e);
	}
}
