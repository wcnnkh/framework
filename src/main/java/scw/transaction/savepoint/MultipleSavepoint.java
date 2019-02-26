package scw.transaction.savepoint;

import java.util.LinkedList;

import scw.transaction.TransactionException;

public class MultipleSavepoint extends LinkedList<Savepoint> implements Savepoint {
	private static final long serialVersionUID = 1L;

	public void rollback() throws TransactionException {
		for (Savepoint savepoint : this) {
			savepoint.rollback();
		}
	}

	public void release() throws TransactionException {
		for (Savepoint savepoint : this) {
			savepoint.release();
		}
	}
}
