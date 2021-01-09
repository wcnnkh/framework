package scw.transaction;

import java.util.LinkedList;

final class TransactionLifecycleCollection extends LinkedList<TransactionLifecycle> implements TransactionLifecycle {
	private static final long serialVersionUID = 1L;

	public void beforeCommit() throws Throwable {
		for (TransactionLifecycle lifeCycle : this) {
			lifeCycle.beforeCommit();
		}
	}

	public void afterCommit(){
		for (TransactionLifecycle lifeCycle : this) {
			lifeCycle.afterCommit();
		}
	}

	public void beforeRollback() {
		for (TransactionLifecycle lifeCycle : this) {
			lifeCycle.beforeRollback();
		}
	}

	public void afterRollback() {
		for (TransactionLifecycle lifeCycle : this) {
			lifeCycle.afterRollback();
		}
	}

	public void completion() {
		for (TransactionLifecycle lifeCycle : this) {
			lifeCycle.completion();
		}
	}

}
