package scw.common.transaction.exception;

public class TransactionProcessException extends TransactionException {
	private static final long serialVersionUID = 1L;

	public TransactionProcessException(Throwable e) {
		super(e);
	}

	public TransactionProcessException(String msg, Throwable e) {
		super(msg, e);
	}
}
