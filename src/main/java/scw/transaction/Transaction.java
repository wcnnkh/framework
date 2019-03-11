package scw.transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.exception.AlreadyExistsException;
import scw.transaction.savepoint.MultipleSavepoint;
import scw.transaction.savepoint.Savepoint;

public final class Transaction {
	private Transaction parent;
	private Map<Object, TransactionResource> resourceMap;
	private TransactionDefinition transactionDefinition;
	private TransactionLifeCycleCollection tlcc;
	private Savepoint savepoint;
	private final boolean active;
	private final boolean newTransaction;
	private boolean complete;
	private boolean rollbackOnly;// 此事务直接回滚，不再提供服务

	/**
	 * 创建一个新的事务
	 * 
	 * @param transactionDefinition
	 * @param active
	 */
	protected Transaction(TransactionDefinition transactionDefinition, boolean active) {
		this.active = active;
		this.newTransaction = true;
		this.transactionDefinition = transactionDefinition;
	}

	/**
	 * 创建一个旧的
	 * 
	 * @param mcts
	 */
	protected Transaction(Transaction mcts) {
		this.active = mcts.active;
		this.newTransaction = false;
		this.parent = mcts;
	}
	
	private void checkStatus(){
		if(complete){
			throw new TransactionException("当前事务已经结束，不能进行后序操作");
		}
		
		if (rollbackOnly) {// 当前事务应该直接回滚，不能继续操作了
			throw new TransactionException("当前事务已经被设置为只能回滚，不能进行后序操作");
		}
	}

	public void transactionLifeCycle(TransactionLifeCycle tlc) {
		checkStatus();

		if (parent != null) {
			parent.transactionLifeCycle(tlc);
			return;
		}

		if (tlcc == null) {
			tlcc = new TransactionLifeCycleCollection();
		}
		tlcc.add(tlc);
	}

	public TransactionResource getResource(Object name) {
		checkStatus();
		
		if (parent != null) {
			return parent.getResource(name);
		}

		return resourceMap == null ? null : resourceMap.get(name);
	}

	public void bindResource(Object name, TransactionResource resource) {
		checkStatus();
		
		if (parent != null) {
			parent.bindResource(name, resource);
			return;
		}

		if (resourceMap == null) {
			resourceMap = new HashMap<Object, TransactionResource>(4, 1);
		} else {
			if (resourceMap.containsKey(name)) {
				throw new AlreadyExistsException("已经存在此事务资源了，不可以重复绑定：" + name);
			}
		}

		resourceMap.put(name, resource);
	}

	protected void createTempSavePoint() {
		this.savepoint = createSavepoint();
	}

	private TransactionSynchronizationLifeCycle tslc;

	private void init() {
		if (isNewTransaction()) {
			if (tslc != null) {
				return;
			}

			TransactionSynchronizationCollection stsc = new TransactionSynchronizationCollection();
			if (resourceMap != null) {
				for (Entry<Object, TransactionResource> entry : resourceMap.entrySet()) {
					stsc.add(new TransactionResourceSynchronization(entry.getValue()));
				}
			}

			tslc = new TransactionSynchronizationLifeCycle(stsc, tlcc);
		}
	}

	protected void process() throws TransactionException {
		if (rollbackOnly) {
			return;
		}

		if (!isNewTransaction()) {
			return;
		}

		init();
		if (tslc != null) {
			tslc.process();
		}
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public void setRollbackOnly(boolean rollbackOnly) {
		this.rollbackOnly = rollbackOnly;
	}

	protected void rollback() throws TransactionException {
		if (!isNewTransaction()) {
			return;
		}

		init();
		if (hasSavepoint()) {
			savepoint.rollback();
		}

		if (tslc != null) {
			tslc.rollback();
		}
	}

	protected void end() {
		if (!isNewTransaction()) {
			return;
		}

		init();
		try {
			if (hasSavepoint()) {
				savepoint.release();
			}

			if (tslc != null) {
				tslc.end();
			}
		} finally {
			complete = true;
		}
	}

	private Savepoint createSavepoint() throws TransactionException {
		checkStatus();
		
		if (resourceMap == null) {
			return null;
		}

		return new MultipleSavepoint(resourceMap.values());
	}

	public boolean hasSavepoint() {
		return savepoint != null;
	}

	public Object getSavepoint() {
		return savepoint;
	}

	public boolean isNewTransaction() {
		return newTransaction;
	}

	public boolean isActive() {
		return active;
	}

	public TransactionDefinition getTransactionDefinition() {
		if (parent != null) {
			return parent.getTransactionDefinition();
		}
		return transactionDefinition;
	}

	protected Transaction getParent() {
		return parent;
	}

	public boolean isComplete() {
		return complete;
	}
}
