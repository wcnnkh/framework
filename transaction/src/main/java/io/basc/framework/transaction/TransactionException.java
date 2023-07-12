package io.basc.framework.transaction;

public class TransactionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for TransactionException.
	 * 
	 * @param msg the detail message
	 */
	public TransactionException(String msg) {
		super(msg);
	}

	public TransactionException(Throwable e) {
		super(e);
	}

	/**
	 * Constructor for TransactionException.
	 * 
	 * @param msg   the detail message
	 * @param cause the root cause from the transaction API in use
	 */
	public TransactionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
