package scw.transaction.synchronization;

import java.util.LinkedList;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

/**
 * 他应该是单例的
 * 
 * @author shuchaowen
 *
 */
public abstract class AbstractTransactionManager implements TransactionManager {
	private static final ThreadLocal<LinkedList<TransactionInfo>> LOCAL = new ThreadLocal<LinkedList<TransactionInfo>>();
	
	public abstract AbstractTransaction newTransaction(AbstractTransaction parent,
			TransactionDefinition transactionDefinition, boolean active) throws TransactionException;

	/**
	 * 获取当前现在运行的事务
	 * 
	 * @return
	 */
	public static final AbstractTransaction getRuntimeTransaction() {
		LinkedList<TransactionInfo> linkedList = LOCAL.get();
		if (linkedList == null) {
			return null;
		}

		TransactionInfo transactionInfo = linkedList.getLast();
		if (transactionInfo == null) {
			return null;
		}

		return transactionInfo.getConcurrentTransaction();
	}

	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		LinkedList<TransactionInfo> linkedList = LOCAL.get();
		TransactionInfo transactionInfo;
		if (linkedList == null) {
			linkedList = new LinkedList<TransactionInfo>();
			LOCAL.set(linkedList);
			transactionInfo = new TransactionInfo(null, transactionDefinition, this);
		} else {
			transactionInfo = linkedList.getLast();
			transactionInfo = new TransactionInfo(transactionInfo, transactionInfo.getTransactionDefinition(), this);
		}
		linkedList.add(transactionInfo);
		return transactionInfo.getTransaction(transactionDefinition);
	}

	public void rollback(Transaction transaction) throws TransactionException {
		AbstractTransaction tx = (AbstractTransaction) transaction;
		LinkedList<TransactionInfo> linkedList = LOCAL.get();
		TransactionInfo transactionInfo = linkedList.getLast();
		if (!transaction.isActive()) {
			if (tx.isNewTransaction()) {
				try {
					transactionInfo.triggerComplete();
				} finally {
					tx.rollback();
				}
			}
			return;
		}

		if (transactionInfo.hasSavepoint()) {
			try {
				tx.rollbackToSavepoint(transactionInfo.getSavepoint());
			} finally {
				try {
					if (tx.isNewTransaction()) {
						tx.rollback();
					}
				} finally {
					linkedList.removeLast();
				}
			}
		} else {
			try {
				if (tx.isNewTransaction()) {
					tx.rollback();
				}
			} finally {
				linkedList.removeLast();
			}
		}
	}

	public void commit(Transaction transaction) throws TransactionException {
		LinkedList<TransactionInfo> linkedList = LOCAL.get();
		TransactionInfo transactionInfo = linkedList.getLast();
		try {
			if (transaction.isNewTransaction()) {
				transactionInfo.triggerBeforeCommit();
				((AbstractTransaction) transaction).commit();
				transactionInfo.triggerAfterCommit();
			}
		} finally {
			linkedList.removeLast();
		}
	}
}
