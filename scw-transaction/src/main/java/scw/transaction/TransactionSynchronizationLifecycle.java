package scw.transaction;

final class TransactionSynchronizationLifecycle implements TransactionSynchronization {
	private final TransactionLifecycle transactionLifeCycle;
	private final TransactionSynchronization transactionSynchronization;

	public TransactionSynchronizationLifecycle(TransactionSynchronization transactionSynchronization,
			TransactionLifecycle transactionLifeCycle) {
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
