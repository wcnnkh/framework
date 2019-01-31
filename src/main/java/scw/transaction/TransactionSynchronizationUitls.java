package scw.transaction;

public class TransactionSynchronizationUitls {

	public static void execute(TransactionSynchronization synchronization) {
		try {
			synchronization.afterCommit();
			synchronization.commit();
			synchronization.afterCommit();
		} catch (Throwable e) {
			synchronization.rollback();
			throw throwTransactionExpetion(e);
		} finally {
			synchronization.complete();
		}
	}

	public static TransactionException throwTransactionExpetion(Throwable e) {
		if (e instanceof TransactionException) {
			return (TransactionException) e;
		}
		return new TransactionException(e);
	}
}
