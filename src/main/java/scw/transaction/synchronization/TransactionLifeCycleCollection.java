package scw.transaction.synchronization;

import java.util.LinkedList;

public class TransactionLifeCycleCollection extends LinkedList<TransactionLifeCycle> implements TransactionLifeCycle {
	private static final long serialVersionUID = 1L;

	public void beforeCommit() throws Throwable {
		for (TransactionLifeCycle lifeCycle : this) {
			try {
				lifeCycle.beforeCommit();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void afterCommit() throws Throwable {
		for (TransactionLifeCycle lifeCycle : this) {
			try {
				lifeCycle.afterCommit();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void beforeRollback() throws Throwable {
		for (TransactionLifeCycle lifeCycle : this) {
			try {
				lifeCycle.beforeRollback();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void afterRollback() throws Throwable {
		for (TransactionLifeCycle lifeCycle : this) {
			try {
				lifeCycle.afterRollback();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void complete() throws Throwable {
		for (TransactionLifeCycle lifeCycle : this) {
			try {
				lifeCycle.complete();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}
