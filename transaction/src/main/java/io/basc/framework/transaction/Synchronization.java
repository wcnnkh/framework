package io.basc.framework.transaction;

/**
 * 事务同步
 * 
 * @author wcnnkh
 *
 */
public interface Synchronization {
	public static final Synchronization EMPTY = new Synchronization() {

		@Override
		public void beforeCompletion() throws Throwable {
		}

		@Override
		public void afterCompletion(Status status) {
		}
	};

	/**
	 * This method is invoked before the start of the commit process. The method
	 * invocation is done in the context of the transaction that is about to be
	 * committed.
	 */
	void beforeCompletion() throws Throwable;

	/**
	 * This method is invoked after the transaction has committed or rolled back.
	 *
	 * @param status The status of the completed transaction.
	 */
	void afterCompletion(Status status);

	default Synchronization and(Synchronization synchronization) {
		if (synchronization == null || synchronization == EMPTY) {
			return this;
		}

		if (this == EMPTY) {
			return synchronization;
		}

		return new MergeSynchronization(this, synchronization);
	}
}