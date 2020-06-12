package scw.transaction;

import java.util.LinkedList;

final class TransactionLifeCycleCollection extends LinkedList<TransactionLifeCycle> implements TransactionLifeCycle {
	private static final long serialVersionUID = 1L;

	public void beforeCommit() throws Throwable {
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.beforeCommit();
		}
	}

	public void afterCommit(){
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.afterCommit();
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

	public void completion() {
		for (TransactionLifeCycle lifeCycle : this) {
			lifeCycle.completion();
		}
	}

}
