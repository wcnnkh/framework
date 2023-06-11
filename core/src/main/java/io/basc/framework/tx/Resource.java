package io.basc.framework.tx;

public interface Resource extends SavepointManager, AutoCloseable {
	public static final Resource EMPTY = new Resource() {

		@Override
		public void rollback() {
		}

		@Override
		public void close() {
		}

		@Override
		public void commit() throws Throwable {
		}

	};

	void commit() throws Throwable;

	void rollback();

	void close();

	@Override
	default Savepoint createSavepoint() throws TransactionException {
		return Savepoint.EMPTY;
	}

	default Resource and(Resource resource) {
		if (resource == null || resource == EMPTY) {
			return this;
		}

		if (this == EMPTY) {
			return resource;
		}

		return new MergeResource(this, resource);
	}
}