package scw.transaction.synchronization;

import scw.transaction.TransactionException;

public class OldAbstractTransaction extends AbstractTransaction {
	private final AbstractTransaction tx;

	public OldAbstractTransaction(AbstractTransaction tx) {
		super(tx.isActive());
		this.tx = tx;
		setNewTransaction(false);
	}

	public boolean hasSavepoint() {
		return tx.hasSavepoint();
	}

	public Object createSavepoint() throws TransactionException {
		return tx.createSavepoint();
	}

	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		tx.rollbackToSavepoint(savepoint);
	}

	public void releaseSavepoint(Object savepoint) throws TransactionException {
		tx.rollbackToSavepoint(savepoint);
	}

	public void begin() throws TransactionException {
		tx.begin();
	}

	public void end() {
		tx.end();
	}

	public void commit() throws TransactionException {
		tx.commit();
	}

	public void rollback() throws TransactionException {
		tx.rollback();
	}
}
