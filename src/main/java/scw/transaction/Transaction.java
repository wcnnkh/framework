package scw.transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.exception.AlreadyExistsException;
import scw.transaction.savepoint.MultipleSavepoint;
import scw.transaction.savepoint.Savepoint;

public final class Transaction {
	private final Transaction parent;
	private final boolean active;
	private final boolean newTransaction;
	private final TransactionDefinition transactionDefinition;

	private Map<Object, Object> resourceMap;
	private TransactionLifeCycleCollection tlcc;
	private Savepoint savepoint;
	private boolean complete;
	private boolean rollbackOnly;// 此事务直接回滚，不再提供服务

	protected Transaction(Transaction parent, TransactionDefinition transactionDefinition, boolean active) {
		this.parent = parent;
		this.active = active;
		this.newTransaction = true;
		this.transactionDefinition = transactionDefinition;
	}

	protected Transaction(Transaction parent, TransactionDefinition transactionDefinition) {
		this.parent = parent;
		this.active = parent.isActive();
		this.newTransaction = false;
		this.transactionDefinition = transactionDefinition;
	}

	private void checkStatus() {
		if (complete) {
			throw new TransactionException("当前事务已经结束，不能进行后序操作");
		}

		if (rollbackOnly) {// 当前事务应该直接回滚，不能继续操作了
			throw new TransactionException("当前事务已经被设置为只能回滚，不能进行后序操作");
		}
	}

	public void transactionLifeCycle(TransactionLifeCycle tlc) {
		checkStatus();

		if (!newTransaction) {
			parent.transactionLifeCycle(tlc);
			return;
		}

		if (tlcc == null) {
			tlcc = new TransactionLifeCycleCollection();
		}
		tlcc.add(tlc);
	}

	public Object getResource(Object name) {
		checkStatus();

		if (!newTransaction) {
			return parent.getResource(name);
		}

		return resourceMap == null ? null : resourceMap.get(name);
	}

	public void bindResource(Object name, Object resource) {
		checkStatus();

		if (!newTransaction) {
			parent.bindResource(name, resource);
			return;
		}

		if (resourceMap == null) {
			resourceMap = new HashMap<Object, Object>(8, 1);
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
		tslc = new TransactionSynchronizationLifeCycle(stsc, tlcc);
	}

	protected void process() throws Throwable {
		if (isComplete()) {
			return;
		}

		if (isRollbackOnly()) {
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
		if (complete) {
			return;
		}

		if (savepoint != null) {
			savepoint.rollback();
		}

		if (!newTransaction) {
			return;
		}

		init();
		if (tslc != null) {
			tslc.rollback();
		}
	}

	protected void end() {
		if (complete) {
			return;
		}

		complete = true;

		if (savepoint != null) {
			try {
				savepoint.release();
			} finally {
				savepoint = null;
			}
		}

		if (!newTransaction) {
			return;
		}

		init();
		if (tslc != null) {
			tslc.end();
		}
	}

	private Savepoint createSavepoint() throws TransactionException {
		checkStatus();

		if (resourceMap == null) {
			return null;
		}

		return new MultipleSavepoint(getTransactionResources());
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
		return transactionDefinition;
	}

	protected Transaction getParent() {
		return parent;
	}

	public boolean isComplete() {
		return complete;
	}
}
