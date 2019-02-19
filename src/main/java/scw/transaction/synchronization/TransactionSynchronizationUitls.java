package scw.transaction.synchronization;

import scw.transaction.TransactionException;

public abstract class TransactionSynchronizationUitls {

	public static void execute(TransactionSynchronization synchronization) {
		try {
			synchronization.process();
		} catch (Throwable e) {
			try {
				synchronization.rollback();
			} finally {
				synchronization.end();
			}
			throw throwTransactionExpetion(e);
		} finally {
			synchronization.end();
		}
	}

	public static TransactionException throwTransactionExpetion(Throwable e) {
		if (e instanceof TransactionException) {
			return (TransactionException) e;
		}
		return new TransactionException(e);
	}

}
