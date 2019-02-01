package scw.transaction.tcc;

import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.synchronization.AbstractTransaction;
import scw.transaction.synchronization.AbstractTransactionManager;

public class TccTransactionManager extends AbstractTransactionManager{

	@Override
	public AbstractTransaction newTransaction(AbstractTransaction transaction,
			TransactionDefinition transactionDefinition, boolean active) throws TransactionException {
		
		return null;
	}
	
}
