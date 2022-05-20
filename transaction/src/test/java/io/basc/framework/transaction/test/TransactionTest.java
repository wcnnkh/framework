package io.basc.framework.transaction.test;

import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionLifecycle;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionUtils;

import org.junit.Assert;
import org.junit.Test;

public class TransactionTest {
	@Test
	public void success() {
		System.out.println("-----------success----------");
		TransactionManager manager = TransactionUtils.getManager();
		Transaction transaction = manager
				.getTransaction(TransactionDefinition.DEFAULT);
		TestTransactionLifecycle lifecycle = new TestTransactionLifecycle();
		try {
			transaction.addLifecycle(lifecycle);
			manager.commit(transaction);
		} catch (Throwable e) {
			manager.rollback(transaction);
		}
		Assert.assertTrue(lifecycle.getValue() == 8);
	}

	private static void throwError() {
		throw new RuntimeException();
	}

	@Test
	public void error() {
		System.out.println("----------------error-----------------");
		TransactionManager manager = TransactionUtils.getManager();
		Transaction transaction = manager
				.getTransaction(TransactionDefinition.DEFAULT);
		TestTransactionLifecycle lifecycle = new TestTransactionLifecycle();
		try {
			transaction.addLifecycle(lifecycle);
			throwError();
			manager.commit(transaction);
		} catch (Throwable e) {
			manager.rollback(transaction);
		}
		Assert.assertTrue(lifecycle.getValue() == 12);
	}

	private static class TestTransactionLifecycle implements
			TransactionLifecycle {
		private int value = 0;

		public int getValue() {
			return value;
		}

		public void beforeCommit() throws Throwable {
			System.out.println("beforeCommit");
			value += 1;
		}

		public void afterCommit() {
			System.out.println("afterCommit");
			value += 2;
		}

		public void beforeRollback() {
			System.out.println("beforeRollback");
			value += 3;
		}

		public void afterRollback() {
			System.out.println("afterRollback");
			value += 4;
		}

		public void complete() {
			System.out.println("complete");
			value += 5;
		}

	}
}
