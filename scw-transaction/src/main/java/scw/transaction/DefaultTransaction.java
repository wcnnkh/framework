package scw.transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Assert;

public final class DefaultTransaction implements Transaction{
	private final DefaultTransaction parent;
	private final boolean active;
	private final boolean newTransaction;
	private final TransactionDefinition transactionDefinition;

	private Map<Object, Object> resourceMap;
	private TransactionLifecycleCollection tlcc;
	private Savepoint tempSavepoint;
	private boolean complete;
	private boolean rollbackOnly;// 此事务直接回滚，不再提供服务
	private boolean commit;// 是否已经提交

	/**
	 * 创建一个新的事务
	 * 
	 * @param parent
	 * @param transactionDefinition
	 * @param active
	 */
	protected DefaultTransaction(DefaultTransaction parent, TransactionDefinition transactionDefinition, boolean active) {
		this.parent = parent;
		this.active = active;
		this.newTransaction = true;
		this.transactionDefinition = transactionDefinition;
	}

	/**
	 * 包装旧的事务
	 * 
	 * @param parent
	 * @param transactionDefinition
	 */
	protected DefaultTransaction(DefaultTransaction parent, TransactionDefinition transactionDefinition) {
		this.parent = parent;
		this.active = parent.isActive();
		this.newTransaction = false;
		this.transactionDefinition = transactionDefinition;
	}

	private void checkStatus() {
		if (complete) {
			throw new TransactionException("当前事务已经结束，无法进行后序操作");
		}

		if (rollbackOnly) {// 当前事务应该直接回滚，不能继续操作了
			throw new TransactionException("当前事务已设置为回滚，无法进行后序操作");
		}

		if (isCommit()) {
			throw new TransactionException("当前事务已经提交，无法进行后序操作");
		}
	}

	public void addLifecycle(TransactionLifecycle tlc) {
		Assert.requiredArgument(tlc != null, "transactionLifecycle");
		checkStatus();

		if (!newTransaction) {
			parent.addLifecycle(tlc);
			return;
		}

		if (tlcc == null) {
			tlcc = new TransactionLifecycleCollection();
		}
		tlcc.add(tlc);
	}

	@SuppressWarnings("unchecked")
	public <T> T getResource(Object name) {
		if (!newTransaction) {
			return parent.getResource(name);
		}

		return (T) (resourceMap == null ? null : resourceMap.get(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T bindResource(Object name, T resource) {
		checkStatus();

		if (!newTransaction) {
			return parent.bindResource(name, resource);
		}

		Object resourceToUse = null;
		if (resourceMap == null) {
			resourceMap = new HashMap<Object, Object>(4);
		} else {
			resourceToUse = resourceMap.get(name);
		}
		
		if(resource == null){
			resourceMap.put(name, resource);
			resourceToUse = resource;
		}
		return (T) resourceToUse;
	}

	protected void createTempSavepoint() {
		if (hasSavepoint()) {
			throw new TransactionException("一个事务不能存在多个savepoint");
		}

		this.tempSavepoint = createSavepoint();
	}

	private TransactionSynchronizationLifecycle tslc;

	@SuppressWarnings("unchecked")
	private Collection<TransactionResource> getTransactionResources() {
		if (resourceMap == null) {
			return Collections.EMPTY_LIST;
		}

		LinkedList<TransactionResource> resources = new LinkedList<TransactionResource>();
		for (Entry<Object, Object> entry : resourceMap.entrySet()) {
			Object resource = entry.getValue();
			if (resource instanceof TransactionResource) {
				resources.add((TransactionResource) resource);
			}
		}
		return resources;
	}

	private void init() {
		if (tslc != null) {
			return;
		}

		TransactionSynchronizationCollection stsc = new TransactionSynchronizationCollection();
		stsc.addAll(getTransactionResources());
		tslc = new TransactionSynchronizationLifecycle(stsc, tlcc);
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public boolean setRollbackOnly(boolean rollbackOnly) {
		this.rollbackOnly = rollbackOnly;
		return true;
	}

	public Savepoint createSavepoint() {
		checkStatus();
		if (resourceMap == null) {
			return new EmptySavepoint();
		}

		return new MultipleSavepoint(getTransactionResources());
	}

	public boolean hasSavepoint() {
		return tempSavepoint != null;
	}

	public boolean isNew() {
		return newTransaction;
	}

	public boolean isActive() {
		return active;
	}

	public TransactionDefinition getDefinition() {
		return transactionDefinition;
	}

	protected DefaultTransaction getParent() {
		return parent;
	}

	public boolean isComplete() {
		return complete;
	}

	protected void commit() throws Throwable {
		if (isComplete()) {
			return;
		}

		if (isRollbackOnly()) {
			return;
		}

		if (!isNew()) {
			return;
		}

		commit = true;
		init();
		if (tslc != null) {
			tslc.commit();
		}
	}

	/**
	 * 是否已经提交
	 * 
	 * @return
	 */
	public boolean isCommit() {
		return isNew() ? commit : parent.commit;
	}

	protected void rollback() throws TransactionException {
		if (complete) {
			return;
		}

		if (tempSavepoint != null) {
			tempSavepoint.rollback();
		}

		if (!newTransaction) {
			return;
		}

		commit = true;
		init();
		if (tslc != null) {
			tslc.rollback();
		}
	}

	protected void completion() {
		if (complete) {
			return;
		}

		complete = true;

		if (tempSavepoint != null) {
			try {
				tempSavepoint.release();
			} finally {
				tempSavepoint = null;
			}
		}

		if (!newTransaction) {
			return;
		}

		init();
		if (tslc != null) {
			tslc.completion();
		}
	}
}
