package scw.transaction;

import java.util.LinkedList;

final class TransactionLifeCycleCollection extends LinkedList<TransactionLifeCycle> implements TransactionLifeCycle {
	private static final long serialVersionUID = 1L;

	public void beforeProcess() throws Throwable {
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.beforeProcess();
		}
	}

	public void afterProcess() {
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.afterProcess();
		}
	}

	public void beforeRollback() {
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.beforeRollback();
		}
	}

	public void afterRollback() {
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.afterRollback();
		}
	}

	public void complete() {
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.complete();
		}
	}

}
