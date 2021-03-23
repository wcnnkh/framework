package scw.transaction.test;

import org.junit.Test;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionLifecycle;
import scw.transaction.TransactionManager;
import scw.transaction.TransactionUtils;

public class TransactionTest {
	@Test
	public void success(){
		System.out.println("-----------success----------");
		TransactionManager manager = TransactionUtils.getManager();
		Transaction transaction = manager.getTransaction(TransactionDefinition.DEFAULT);
		try {
			transaction.addLifecycle(new TestTransactionLifecycle());
			manager.commit(transaction);
		} catch (Throwable e) {
			manager.rollback(transaction);
		}
	}
	
	private static void throwError(){
		throw new RuntimeException();
	}
	
	@Test
	public void error(){
		System.out.println("----------------error-----------------");
		TransactionManager manager = TransactionUtils.getManager();
		Transaction transaction = manager.getTransaction(TransactionDefinition.DEFAULT);
		try {
			transaction.addLifecycle(new TestTransactionLifecycle());
			throwError();
			manager.commit(transaction);
		} catch (Throwable e) {
			manager.rollback(transaction);
		}
	}
	
	private static class TestTransactionLifecycle implements TransactionLifecycle{

		public void beforeCommit() throws Throwable {
			System.out.println("beforeCommit");
		}

		public void afterCommit() {
			System.out.println("afterCommit");
		}

		public void beforeRollback() {
			System.out.println("beforeRollback");
		}

		public void afterRollback() {
			System.out.println("afterRollback");
		}

		public void complete() {
			System.out.println("complete");
		}
		
	}
}
