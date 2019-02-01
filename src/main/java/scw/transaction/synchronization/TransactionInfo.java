package scw.transaction.synchronization;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;

class TransactionInfo {
	private final TransactionInfo parent;
	private AbstractTransaction transaction;
	private Object savepoint;
	private final AbstractTransactionManager manager;

	public TransactionInfo(TransactionInfo parent, AbstractTransactionManager manager) {
		this.parent = parent;
		this.manager = manager;
	}

	public AbstractTransaction getConcurrentTransaction() {
		return transaction;
	}

	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		transaction = parent.getConcurrentTransaction() == null ? null
				: new OldTransaction(parent.getConcurrentTransaction());
		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (transaction == null || !transaction.isActive()) {
				transaction = manager.newTransaction(transaction, transactionDefinition, true);
			}
			break;
		case SUPPORTS:
			if (transaction == null) {
				transaction = manager.newTransaction(transaction, transactionDefinition, false);
			}
			break;
		case MANDATORY:
			if (transaction == null || !transaction.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case REQUIRES_NEW:
			transaction = manager.newTransaction(transaction, transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			transaction = manager.newTransaction(transaction, transactionDefinition, false);
			break;
		case NEVER:
			if (transaction != null && transaction.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case NESTED:
			if (transaction != null && transaction.isActive()) {
				savepoint = transaction.createSavepoint();
			} else {
				transaction = manager.newTransaction(transaction, transactionDefinition, true);
			}
			break;
		}
		return transaction;
	}

	public Object getSavepoint() {
		return savepoint;
	}

	public boolean hasSavepoint() {
		return transaction != null;
	}
}

class OldTransaction extends AbstractTransaction {
	private final AbstractTransaction tx;

	public OldTransaction(AbstractTransaction tx) {
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

	@Override
	protected void rollback() throws TransactionException {
		tx.rollback();
	}

	@Override
	protected void commit() throws TransactionException {
		tx.commit();
	}
}
