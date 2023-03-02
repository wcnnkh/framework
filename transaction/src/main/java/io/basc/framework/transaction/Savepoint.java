package io.basc.framework.transaction;

/**
 * 保存点
 * 
 * @author wcnnkh
 *
 */
public interface Savepoint {
	public static Savepoint EMPTY = new EmptySavepoint();

	void rollback();

	void release();

	default Savepoint and(Savepoint savepoint) {
		if (savepoint == null || savepoint == EMPTY) {
			return this;
		}

		if (this == EMPTY) {
			return savepoint;
		}

		return new MergeSavepoint(this, savepoint);
	}
}
