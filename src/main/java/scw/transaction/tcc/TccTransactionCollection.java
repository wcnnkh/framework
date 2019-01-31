package scw.transaction.tcc;

import java.util.Iterator;
import java.util.LinkedList;

import scw.transaction.TransactionException;

public final class TccTransactionCollection extends LinkedList<TccTransactionItem> implements TccTransactionItem {
	private static final long serialVersionUID = 1L;
	private int beginTag = 0;
	private int processTag = 0;

	public void begin() throws TransactionException {
		Iterator<TccTransactionItem> iterator = iterator();
		for (; iterator.hasNext(); beginTag++) {
			TccTransactionItem transaction = iterator.next();
			if (transaction != null) {
				transaction.begin();
			}
		}
	}

	public void process() throws TransactionException {
		Iterator<TccTransactionItem> iterator = iterator();
		for (; iterator.hasNext(); processTag++) {
			TccTransactionItem transaction = iterator.next();
			if (transaction != null) {
				transaction.process();
			}
		}
	}

	public void end() throws TransactionException {
		Iterator<TccTransactionItem> iterator = iterator();
		for (; beginTag >= 0 && iterator.hasNext(); beginTag--) {
			TccTransactionItem transaction = iterator.next();
			if (transaction != null) {
				try {
					transaction.end();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void rollback() throws TransactionException {
		Iterator<TccTransactionItem> iterator = iterator();
		for (; processTag >= 0 && iterator.hasNext(); processTag--) {
			TccTransactionItem transaction = iterator.next();
			if (transaction != null) {
				try {
					transaction.rollback();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
