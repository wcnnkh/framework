package scw.transaction.tcc;

import scw.transaction.TransactionException;
import scw.transaction.synchronization.AbstractTransaction;

public class TccTransaction extends AbstractTransaction {

	public TccTransaction(boolean active) {
		super(active);
	}

	public boolean hasSavepoint() {
		// TODO Auto-generated method stub
		return false;
	} 

	public Object createSavepoint() throws TransactionException {
		// TODO Auto-generated method stub
		return null;
	}

	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		// TODO Auto-generated method stub
		
	}

	public void releaseSavepoint(Object savepoint) throws TransactionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void rollback() throws TransactionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void commit() throws TransactionException {
		// TODO Auto-generated method stub
		
	}

}
