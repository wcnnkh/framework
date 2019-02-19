package scw.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import scw.sql.ConnectionFactory;

public abstract class TransactionManager {
	private static final ThreadLocal<LinkedList<TransactionSynchronizationContext>> LOCAL = new ThreadLocal<LinkedList<TransactionSynchronizationContext>>();

	/**
	 * 当前上下文是否存在事务
	 * 
	 * @return
	 */
	public static boolean hasTransaction() {
		LinkedList<TransactionSynchronizationContext> list = LOCAL.get();
		if (list == null) {
			return false;
		}

		return !list.isEmpty();
	}

	/**
	 * 如果当前没有事务就是空的
	 * 
	 * @param connectionFactory
	 * @return
	 * @throws SQLException
	 */
	public static Connection getCurrentConnection(ConnectionFactory connectionFactory) throws SQLException {
		LinkedList<TransactionSynchronizationContext> list = LOCAL.get();
		if (list == null) {
			return null;
		}

		TransactionSynchronizationContext mcts = list.getLast();
		if (mcts == null) {
			return null;
		}

		return mcts.getConnection(connectionFactory);
	}

	public static void transactionSynchronization(TransactionSynchronization ts) throws TransactionException {
		LinkedList<TransactionSynchronizationContext> list = LOCAL.get();
		if (list == null) {
			execute(ts);
			return;
		}

		TransactionSynchronizationContext mcts = list.getLast();
		if (mcts == null) {
			execute(ts);
			return;
		}

		mcts.transactionSynchronization(ts);
	}

	/*
	 * 监听当前事务的生命周期，如果不存在事务则无效
	 */
	public static void transactionLifeCycle(TransactionLifeCycle tlc) {
		LinkedList<TransactionSynchronizationContext> list = LOCAL.get();
		if (list == null) {
			return;
		}

		TransactionSynchronizationContext mcts = list.getLast();
		if (mcts == null) {
			return;
		}

		mcts.transactionLifeCycle(tlc);
	}
	
	public static TransactionSynchronizationContext getTransaction(
			TransactionDefinition transactionDefinition) {
		LinkedList<TransactionSynchronizationContext> list = LOCAL.get();
		TransactionSynchronizationContext mcts = null;
		if (list == null) {
			list = new LinkedList<TransactionSynchronizationContext>();
			LOCAL.set(list);
		} else {
			mcts = list.getLast();
			if (mcts != null) {
				mcts = new TransactionSynchronizationContext(mcts);
			}
		}

		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (mcts == null) {
				mcts = new TransactionSynchronizationContext(transactionDefinition, true);
			}
			break;
		case SUPPORTS:
			if (mcts == null) {
				mcts = new TransactionSynchronizationContext(transactionDefinition, false);
			}
			break;
		case MANDATORY:
			if (mcts == null || !mcts.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case REQUIRES_NEW:
			mcts = new TransactionSynchronizationContext(transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			mcts = new TransactionSynchronizationContext(transactionDefinition, false);
			break;
		case NEVER:
			if (mcts != null && mcts.isActive()) {
				throw new TransactionException(transactionDefinition.getPropagation().name());
			}
			break;
		case NESTED:
			if (mcts != null && mcts.isActive()) {
				mcts.createTempSavePoint();
			} else if (mcts == null) {
				mcts = new TransactionSynchronizationContext(transactionDefinition, true);
			}
			break;
		}
		list.addLast(mcts);
		return mcts;
	}

	public static void process(TransactionSynchronizationContext mcts) throws TransactionException {
		LinkedList<TransactionSynchronizationContext> list = LOCAL.get();
		if (list == null) {
			throw new TransactionException("不存在事务");
		}

		TransactionSynchronizationContext currentMcts = list.getLast();
		if (mcts != currentMcts) {
			throw new TransactionException("事务需要顺序关闭，请先关闭子事务");
		}

		try {
			mcts.process();
		} finally {
			try {
				mcts.end();
			} finally {
				list.removeLast();
				if (list.isEmpty()) {
					LOCAL.remove();
				}
			}
		}
	}

	public static void rollback(TransactionSynchronizationContext mcts) throws TransactionException {
		LinkedList<TransactionSynchronizationContext> list = LOCAL.get();
		if (list == null) {
			throw new TransactionException("不存在事务");
		}

		TransactionSynchronizationContext currentMcts = list.getLast();
		if (mcts != currentMcts) {
			throw new TransactionException("事务需要顺序关闭，请先关闭子事务");
		}

		try {
			mcts.rollback();
		} finally {
			try {
				mcts.end();
			} finally {
				list.removeLast();
				if (list.isEmpty()) {
					LOCAL.remove();
				}
			}
		}
	}

	private static void execute(TransactionSynchronization synchronization) {
		try {
			synchronization.process();
		} catch (Throwable e) {
			try {
				synchronization.rollback();
			} finally {
				synchronization.end();
			}
			throw throwTransactionExpetion(e);
		} finally {
			synchronization.end();
		}
	}

	private static TransactionException throwTransactionExpetion(Throwable e) {
		if (e instanceof TransactionException) {
			return (TransactionException) e;
		}
		return new TransactionException(e);
	}
}
