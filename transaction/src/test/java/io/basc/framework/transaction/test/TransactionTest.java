package io.basc.framework.transaction.test;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.transaction.Synchronization;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionManager;
import io.basc.framework.transaction.TransactionStatus;
import io.basc.framework.transaction.TransactionUtils;

public class TransactionTest {
	@Test
	public void success() {
		System.out.println("-----------success----------");
		TransactionManager manager = TransactionUtils.getManager();
		Transaction transaction = manager.getTransaction(TransactionDefinition.DEFAULT);
		TestTransactionLifecycle lifecycle = new TestTransactionLifecycle();
		try {
			transaction.registerSynchronization(lifecycle);
			manager.commit(transaction);
		} catch (Throwable e) {
			manager.rollback(transaction);
		}
		System.out.println("success" + lifecycle.getValue());
		Assert.assertTrue(lifecycle.getValue() == 14);
	}

	private static void throwError() {
		throw new RuntimeException();
	}

	@Test
	public void error() {
		System.out.println("----------------error-----------------");
		TransactionManager manager = TransactionUtils.getManager();
		Transaction transaction = manager.getTransaction(TransactionDefinition.DEFAULT);
		TestTransactionLifecycle lifecycle = new TestTransactionLifecycle();
		try {
			transaction.registerSynchronization(lifecycle);
			throwError();
			manager.commit(transaction);
		} catch (Throwable e) {
			manager.rollback(transaction);
		}
		System.out.println("error" + lifecycle.getValue());
		Assert.assertTrue(lifecycle.getValue() == 18);
	}

	private static class TestTransactionLifecycle implements Synchronization {
		private int value = 0;

		public int getValue() {
			return value;
		}

		@Override
		public void beforeCompletion() {
			System.out.println("beforeCompletion-------------------------");
		}

		@Override
		public void afterCompletion(TransactionStatus status) {
			System.out.println(status);
			value += status.getCode();
		}
	}
}
