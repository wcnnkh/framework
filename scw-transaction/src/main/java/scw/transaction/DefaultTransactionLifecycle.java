package scw.transaction;

import scw.core.Ordered;


public class DefaultTransactionLifecycle implements TransactionLifecycle, Ordered {
	

	public void beforeCommit() throws Throwable {
		// TODO Auto-generated method stub
	}

	public void afterCommit() {
		// TODO Auto-generated method stub
	}

	public void beforeRollback() {
		// TODO Auto-generated method stub
	}

	public void afterRollback() {
		// TODO Auto-generated method stub
	}

	public void complete() {
		// TODO Auto-generated method stub
	}

	public int getOrder() {
		return Ordered.DEFAULT_PRECEDENCE;
	}

}
