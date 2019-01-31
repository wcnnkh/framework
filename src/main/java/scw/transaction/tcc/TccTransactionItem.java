package scw.transaction.tcc;

import scw.transaction.TransactionException;

public interface TccTransactionItem {

	void begin() throws TransactionException;

	void process() throws TransactionException;

	void end() throws TransactionException;

	void rollback() throws TransactionException;
}
