package io.basc.framework.transaction;

public class ManageTransaction implements Transaction{
	
	@Override
	public TransactionDefinition getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addLifecycle(TransactionLifecycle lifecycle) throws TransactionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T getResource(Object name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T bindResource(Object name, T resource) throws TransactionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRollbackOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setRollbackOnly(boolean rollbackOnly) throws TransactionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCompleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSavepoint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void commit() throws Throwable {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		
	}

}
