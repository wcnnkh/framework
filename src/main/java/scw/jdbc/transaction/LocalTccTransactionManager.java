package scw.jdbc.transaction;

import java.util.LinkedList;

import scw.transaction.Transaction;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionManager;

public class LocalTccTransactionManager implements TransactionManager{
	private LinkedList<TransactionManager> managers = new LinkedList<TransactionManager>();
	
	
	
	public Transaction getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
		// TODO Auto-generated method stub
		return null;
	}

	public void commit(Transaction transaction) throws TransactionException {
		// TODO Auto-generated method stub
		
	}

	public void rollback(Transaction transaction) throws TransactionException {
		// TODO Auto-generated method stub
		
	}
	
}
