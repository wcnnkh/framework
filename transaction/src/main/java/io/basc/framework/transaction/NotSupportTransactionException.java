package io.basc.framework.transaction;

public class NotSupportTransactionException extends TransactionException {
	private static final long serialVersionUID = 1L;

	public NotSupportTransactionException(String msg) {
		super(msg);
	}
}
