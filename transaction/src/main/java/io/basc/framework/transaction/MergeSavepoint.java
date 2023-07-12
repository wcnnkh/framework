package io.basc.framework.transaction;

public final class MergeSavepoint implements Savepoint {
	private final Savepoint left;
	private final Savepoint right;

	public MergeSavepoint(Savepoint left, Savepoint right) {
		this.left = left;
		this.right = right;
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
	public void release() {
		try {
			left.release();
		} finally {
			right.release();
		}
	}

}
