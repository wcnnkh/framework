package scw.transaction;

class TransactionResourceSynchronization implements TransactionSynchronization {
	private final TransactionResource transactionResource;

	public TransactionResourceSynchronization(TransactionResource transactionResource) {
		this.transactionResource = transactionResource;
	}

	public void commit() throws Throwable {
		if (transactionResource == null) {
			return;
		}

		transactionResource.commit();
	}

	public void rollback() {
		if (transactionResource == null) {
			return;
		}

		transactionResource.rollback();
	}

	public void completion() {
		if (transactionResource == null) {
			return;
		}

		transactionResource.completion();
	}

}
