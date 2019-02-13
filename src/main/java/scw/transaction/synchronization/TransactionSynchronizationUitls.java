package scw.transaction.synchronization;

import java.util.LinkedList;

import scw.transaction.Transaction;
import scw.transaction.TransactionException;

public abstract class TransactionSynchronizationUitls {
	private static final ThreadLocal<LinkedList<TransactionContext>> LOCAL = new ThreadLocal<LinkedList<TransactionContext>>();

	public static TransactionContext getCurrentTransactionContext() {
		LinkedList<TransactionContext> context = LOCAL.get();
		return context == null ? null : context.getLast();
	}
	
	public static void setCurrentContext(TransactionContext context){
		
	}

	public static void removeCurrentContext() {
		LinkedList<TransactionContext> context = LOCAL.get();
		if (context != null) {
			context.removeLast();
			if (context.isEmpty()) {
				LOCAL.remove();
			}
		}
	}

	/*
	 * private static final ThreadLocal<LinkedList<TransactionInfo>> LOCAL = new
	 * ThreadLocal<LinkedList<TransactionInfo>>();
	 * 
	 * public static void execute(TransactionSynchronization synchronization) {
	 * try { synchronization.begin(); synchronization.commit(); } catch
	 * (Throwable e) { try { synchronization.rollback(); } finally {
	 * synchronization.end(); } throw throwTransactionExpetion(e); } finally {
	 * synchronization.end(); } }
	 * 
	 * public static TransactionException throwTransactionExpetion(Throwable e)
	 * { if (e instanceof TransactionException) { return (TransactionException)
	 * e; } return new TransactionException(e); }
	 * 
	 * public static Transaction getTransaction(AbstractTransactionDefinition
	 * transactionDefinition) throws TransactionException {
	 * LinkedList<TransactionInfo> linkedList = LOCAL.get(); TransactionInfo
	 * transactionInfo; if (linkedList == null) { linkedList = new
	 * LinkedList<TransactionInfo>(); LOCAL.set(linkedList); transactionInfo =
	 * new TransactionInfo(null, transactionDefinition); } else {
	 * transactionInfo = linkedList.getLast(); transactionInfo = new
	 * TransactionInfo(transactionInfo,
	 * transactionInfo.getTransactionDefinition()); }
	 * linkedList.add(transactionInfo); return
	 * transactionInfo.getTransaction(transactionDefinition); }
	 * 
	 * public void rollback(Transaction transaction) throws TransactionException
	 * { AbstractTransaction tx = (AbstractTransaction) transaction;
	 * LinkedList<TransactionInfo> linkedList = LOCAL.get(); TransactionInfo
	 * transactionInfo = linkedList.getLast(); if (!transaction.isActive()) { if
	 * (tx.isNewTransaction()) { try { // transactionInfo.triggerComplete(); }
	 * finally { tx.rollback(); } } return; }
	 * 
	 * if (transactionInfo.hasSavepoint()) { try {
	 * tx.rollbackToSavepoint(transactionInfo.getSavepoint()); } finally { try {
	 * if (tx.isNewTransaction()) { tx.rollback(); } } finally {
	 * linkedList.removeLast(); } } } else { try { if (tx.isNewTransaction()) {
	 * tx.rollback(); } } finally { linkedList.removeLast(); } } }
	 * 
	 * public void commit(Transaction transaction) throws TransactionException {
	 * LinkedList<TransactionInfo> linkedList = LOCAL.get(); TransactionInfo
	 * transactionInfo = linkedList.getLast(); try { if
	 * (transaction.isNewTransaction()) { transactionInfo.begin();
	 * transactionInfo.commit(); } } finally { linkedList.removeLast(); } }
	 */
}

final class TransactionInfo implements TransactionSynchronization {
	private final TransactionInfo parent;
	private AbstractTransaction transaction;
	private final AbstractTransactionDefinition transactionDefinition;
	private Object savepoint;
	private TransactionSynchronizationCollection transactionSynchronizationCollection;
	private TransactionLifeCycleCollection transactionLifeCycleCollection;
	private TransactionSynchronizationLifeCycle tc;

	public TransactionInfo(TransactionInfo parent, AbstractTransactionDefinition transactionDefinition) {
		this.parent = parent;
		this.transactionDefinition = transactionDefinition;
	}

	public AbstractTransaction getConcurrentTransaction() {
		return transaction;
	}

	public void addTransactionSynchronization(TransactionSynchronization transactionSynchronization) {
		if (transactionSynchronizationCollection == null) {
			transactionSynchronizationCollection = new TransactionSynchronizationCollection();
		}
		transactionSynchronizationCollection.add(transactionSynchronization);
	}

	public void addTransactionLifeCycleCollection(TransactionLifeCycle transactionLifeCycle) {
		if (transactionLifeCycleCollection == null) {
			transactionLifeCycleCollection = new TransactionLifeCycleCollection();
		}
		transactionLifeCycleCollection.add(transactionLifeCycle);
	}

	public AbstractTransactionDefinition getTransactionDefinition() {
		return transactionDefinition;
	}

	public Transaction getTransaction(AbstractTransactionDefinition transactionDefinition) throws TransactionException {
		transaction = parent.getConcurrentTransaction() == null ? null
				: new OldAbstractTransaction(parent.getConcurrentTransaction());
		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (transaction == null || !transaction.isActive()) {
				transaction = transactionDefinition.newTransaction(transaction, true);
			}
			break;
		case SUPPORTS:
			if (transaction == null) {
				transaction = transactionDefinition.newTransaction(transaction, false);
			}
			break;
		case MANDATORY:
			if (transaction == null || !transaction.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case REQUIRES_NEW:
			transaction = transactionDefinition.newTransaction(transaction, true);
			break;
		case NOT_SUPPORTED:
			transaction = transactionDefinition.newTransaction(transaction, false);
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
				transaction = transactionDefinition.newTransaction(transaction, true);
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

	public TransactionSynchronizationCollection getTransactionSynchronizationCollection() {
		return transactionSynchronizationCollection;
	}

	public TransactionLifeCycleCollection getTransactionLifeCycleCollection() {
		return transactionLifeCycleCollection;
	}

	public void begin() throws TransactionException {
		TransactionSynchronizationCollection tsc = new TransactionSynchronizationCollection();
		if (transactionSynchronizationCollection != null) {
			tsc.add(transactionSynchronizationCollection);
		}

		if (transaction != null) {
			tsc.add(transaction);
		}
		tc = new TransactionSynchronizationLifeCycle(tsc, transactionLifeCycleCollection);
		tc.begin();
	}

	public void commit() throws TransactionException {
		if (tc != null) {
			tc.commit();
		}
	}

	public void rollback() throws TransactionException {
		if (tc != null) {
			tc.rollback();
		}
	}

	public void end() {
		if (tc != null) {
			tc.end();
		}
	}
}
