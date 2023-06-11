package io.basc.framework.test;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.tx.Status;
import io.basc.framework.tx.Synchronization;
import io.basc.framework.tx.Transaction;
import io.basc.framework.tx.TransactionDefinition;
import io.basc.framework.tx.TransactionManager;
import io.basc.framework.tx.TransactionUtils;

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
		Assert.assertTrue(lifecycle.getValue() == 11);
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
		public void afterCompletion(Status status) {
			System.out.println(status);
			value += status.getCode();
		}
	}
}
