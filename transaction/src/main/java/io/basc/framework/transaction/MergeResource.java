package io.basc.framework.transaction;

public final class MergeResource implements Resource {
	private final Resource left;
	private final Resource right;

	public MergeResource(Resource left, Resource right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public Savepoint createSavepoint() throws TransactionException {
		Savepoint savepoint = left.createSavepoint();
		try {
			savepoint = savepoint.and(right.createSavepoint());
		} catch (Throwable e) {
			try {
				savepoint.rollback();
			} finally {
				savepoint.release();
			}
		}
		return savepoint;
	}

	@Override
	public void commit() throws Throwable {
		left.commit();
		right.commit();
	}

	@Override
	public void rollback() {
		try {
			left.rollback();
		} finally {
			right.rollback();
		}
	}

	@Override
	public void close() {
		try {
			right.close();
		} finally {
			left.close();
		}
	}
}
