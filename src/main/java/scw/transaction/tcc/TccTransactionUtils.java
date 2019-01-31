package scw.transaction.tcc;

import scw.common.transaction.exception.TransactionBeginException;
import scw.common.transaction.exception.TransactionEndException;
import scw.common.transaction.exception.TransactionException;
import scw.common.transaction.exception.TransactionProcessException;
import scw.common.transaction.exception.TransactionRollbackException;

public abstract class TccTransactionUtils {
	
	public static void transaction(TccTransactionItem transaction) {
		try {
			transaction.begin();
		} catch (Throwable e) {
			try {
				transaction.end();
			} catch (Throwable e1) {
				if (e1 instanceof TransactionException) {
					throw (TransactionException) e1;
				}
				throw new TransactionEndException(e1);
			}

			if (e instanceof TransactionException) {
				throw (TransactionException) e;
			}
			throw new TransactionBeginException(e);
		}

		try {
			transaction.process();
		} catch (Throwable e) {
			try {
				transaction.rollback();
			} catch (Throwable e1) {
				if (e1 instanceof TransactionException) {
					throw (TransactionException) e1;
				}

				throw new TransactionRollbackException(e1);
			}

			if (e instanceof TransactionException) {
				throw (TransactionException) e;
			}

			throw new TransactionProcessException(e);
		} finally {
			try {
				transaction.end();
			} catch (Throwable e) {
				if (e instanceof TransactionException) {
					throw (TransactionException) e;
				}

				throw new TransactionEndException(e);
			}
		}
	}
}
