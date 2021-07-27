package scw.transaction;

import scw.core.Ordered;


public class DefaultTransactionLifecycle implements TransactionLifecycle, Ordered {
	

	public void beforeCommit() throws Throwable {
	}

	public void afterCommit() {
	}

	public void beforeRollback() {
	}

	public void afterRollback() {
	}

	public void complete() {
	}

	public int getOrder() {
		return Ordered.DEFAULT_PRECEDENCE;
	}

}
