package scw.transaction.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import scw.sql.ConnectionFactory;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;

public class MultipleConnectionTransactionUtils {
	private static final ThreadLocal<LinkedList<MultipleConnectionTransactionSynchronization>> LOCAL = new ThreadLocal<LinkedList<MultipleConnectionTransactionSynchronization>>();

	private static MultipleConnectionTransactionSynchronization getCurrentTransaction(
			boolean remove) {
		LinkedList<MultipleConnectionTransactionSynchronization> list = LOCAL
				.get();
		if (list == null) {
			return null;
		}

		return remove ? list.removeLast() : list.getLast();
	}

	public static Connection getCurrentConnection(
			ConnectionFactory connectionFactory) throws SQLException {
		MultipleConnectionTransactionSynchronization mcts = getCurrentTransaction(false);
		if (mcts == null) {
			mcts = getTransaction(new DefaultTransactionDefinition());
		}

		return mcts.getConnection(connectionFactory);
	}

	public static MultipleConnectionTransactionSynchronization getTransaction(
			TransactionDefinition transactionDefinition) {
		LinkedList<MultipleConnectionTransactionSynchronization> list = LOCAL
				.get();
		MultipleConnectionTransactionSynchronization mcts = null;
		if (list == null) {
			list = new LinkedList<MultipleConnectionTransactionSynchronization>();
			LOCAL.set(list);
		} else {
			mcts = list.getLast();
			if (mcts != null) {
				mcts = new MultipleConnectionTransactionSynchronization(mcts);
			}
		}

		switch (transactionDefinition.getPropagation()) {
		case REQUIRED:
			if (mcts == null) {
				mcts = new MultipleConnectionTransactionSynchronization(
						transactionDefinition, true);
			}
			break;
		case SUPPORTS:
			if (mcts == null) {
				mcts = new MultipleConnectionTransactionSynchronization(
						transactionDefinition, false);
			}
			break;
		case MANDATORY:
			if (mcts == null || !mcts.isActive()) {
				throw new TransactionException(transactionDefinition
						.getPropagation().name());
			}
			break;
		case REQUIRES_NEW:
			mcts = new MultipleConnectionTransactionSynchronization(
					transactionDefinition, true);
			break;
		case NOT_SUPPORTED:
			mcts = new MultipleConnectionTransactionSynchronization(
					transactionDefinition, false);
			break;
		case NEVER:
			if (mcts != null && mcts.isActive()) {
				throw new TransactionException(transactionDefinition
						.getPropagation().name());
			}
			break;
		case NESTED:
			if (mcts != null && mcts.isActive()) {
				mcts.createTempSavePoint();
			} else if (mcts == null) {
				mcts = new MultipleConnectionTransactionSynchronization(
						transactionDefinition, true);
			}
			break;
		}
		list.addLast(mcts);
		return mcts;
	}

	public void commit(MultipleConnectionTransactionSynchronization mcts)
			throws TransactionException {
		MultipleConnectionTransactionSynchronization currentMcts = getCurrentTransaction(true);
		if (mcts != currentMcts) {
			throw new TransactionException("事务需要顺序关闭，请先关闭子事务");
		}

		try {
			mcts.begin();
			mcts.commit();
		} finally {
			mcts.end();
		}
	}

	public void rollback(MultipleConnectionTransactionSynchronization mcts)
			throws TransactionException {
		MultipleConnectionTransactionSynchronization currentMcts = getCurrentTransaction(true);
		if (mcts != currentMcts) {
			throw new TransactionException("事务需要顺序关闭，请先关闭子事务");
		}

		try {
			mcts.begin();
			mcts.rollback();
		} finally {
			mcts.end();
		}
	}
}
