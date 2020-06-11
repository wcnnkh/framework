package scw.transaction;

final class TransactionSynchronizationLifeCycle implements TransactionSynchronization {
	private final TransactionLifeCycle transactionLifeCycle;
	private final TransactionSynchronization transactionSynchronization;

	public TransactionSynchronizationLifeCycle(TransactionSynchronization transactionSynchronization,
			TransactionLifeCycle transactionLifeCycle) {
		this.transactionLifeCycle = transactionLifeCycle;
		this.transactionSynchronization = transactionSynchronization;
	}

	public void commit() throws Throwable {
		if (transactionLifeCycle != null) {
			transactionLifeCycle.beforeCommit();
		}

		if (transactionSynchronization != null) {
			transactionSynchronization.commit();
		}

		if (transactionLifeCycle != null) {
			transactionLifeCycle.afterCommit();
		}
	}

	public void rollback() {
		if (transactionLifeCycle != null) {
			transactionLifeCycle.beforeRollback();
		}

		if (transactionSynchronization != null) {
			transactionSynchronization.rollback();
		}

		if (transactionLifeCycle != null) {
			transactionLifeCycle.afterRollback();
		}
	}

	public void completion() {
		try {
			if(transactionSynchronization != null){
				transactionSynchronization.completion();
			}
		} finally {
			if (transactionLifeCycle != null) {
				transactionLifeCycle.completion();
			}
		}
	}
}
