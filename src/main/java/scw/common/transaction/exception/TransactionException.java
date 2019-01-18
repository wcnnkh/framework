package scw.common.transaction.exception;

public class TransactionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TransactionException(Throwable e) {
		super(e);
	}

	public TransactionException(String msg, Throwable e) {
		super(msg, e);
	}

	public TransactionException(String msg) {
		super(msg);
	}

	public TransactionException() {
		super();
	}
}
