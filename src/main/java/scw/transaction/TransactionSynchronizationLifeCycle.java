package scw.transaction;

public class TransactionSynchronizationLifeCycle implements TransactionSynchronization {
	private final TransactionLifeCycle transactionLifeCycle;
	private final TransactionSynchronization transactionSynchronization;

	public TransactionSynchronizationLifeCycle(TransactionSynchronization transactionSynchronization,
			TransactionLifeCycle transactionLifeCycle) {
		this.transactionLifeCycle = transactionLifeCycle;
		this.transactionSynchronization = transactionSynchronization;
	}

	public void process() throws TransactionException {
		if (transactionLifeCycle != null) {
			try {
				transactionLifeCycle.beforeProcess();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		if (transactionSynchronization != null) {
			transactionSynchronization.process();
		}

		if (transactionLifeCycle != null) {
			try {
				transactionLifeCycle.afterProcess();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void rollback() throws TransactionException {
		if (transactionLifeCycle != null) {
			try {
				transactionLifeCycle.beforeRollback();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		if (transactionSynchronization != null) {
			transactionSynchronization.rollback();
		}

		if (transactionLifeCycle != null) {
			try {
				transactionLifeCycle.afterRollback();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void end() {
		if (transactionSynchronization == null) {
			if (transactionLifeCycle != null) {
				try {
					transactionLifeCycle.complete();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				transactionSynchronization.end();
			} finally {
				if (transactionLifeCycle != null) {
					try {
						transactionLifeCycle.complete();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
