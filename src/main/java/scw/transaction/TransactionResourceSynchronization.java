package scw.transaction;

class TransactionResourceSynchronization implements TransactionSynchronization {
	private final TransactionResource transactionResource;

	public TransactionResourceSynchronization(TransactionResource transactionResource) {
		this.transactionResource = transactionResource;
	}

	public void process() throws Throwable {
		if (transactionResource == null) {
			return;
		}

		transactionResource.process();
	}

	public void rollback() {
		if (transactionResource == null) {
			return;
		}

		transactionResource.rollback();
	}

	public void end() {
		if (transactionResource == null) {
			return;
		}

		transactionResource.end();
	}

}
