package io.basc.framework.transaction;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public class StandardTransaction implements Transaction, Synchronization {
	private final TransactionDefinition definition;
	private final boolean isNew;
	private final Transaction parent;
	private Map<Object, Object> resourceMap;
	private boolean rollbackOnly;// 标记此事务直接回滚
	private Savepoint savepoint = Savepoint.EMPTY;
	private Status status = Status.UNKNOWN;
	private Synchronization synchronization = Synchronization.EMPTY;
	private Resource resource = Resource.EMPTY;
	private final boolean active;

	/**
	 * 创建一个新的事务
	 * 
	 */
	public StandardTransaction(Transaction parent, TransactionDefinition definition, boolean active) {
		this(parent, definition, active, true, null);
	}

	/**
	 * 包装旧的事务
	 * 
	 */
	public StandardTransaction(Transaction parent, TransactionDefinition definition) {
		this(parent, definition, parent.isActive(), false, null);
	}

	/**
	 * 嵌套事务
	 * 
	 */
	public StandardTransaction(Transaction parent, TransactionDefinition definition, Savepoint savepoint,
			boolean isNew) {
		this(parent, definition, parent.isActive(), isNew, savepoint);
	}

	/**
	 * 自定义
	 * 
	 * @param parent
	 * @param definition
	 * @param isNew
	 * @param savepoint
	 */
	public StandardTransaction(Transaction parent, TransactionDefinition definition, boolean active, boolean isNew,
			@Nullable Savepoint savepoint) {
		Assert.isTrue(!(!isNew && parent == null), "An old transaction must have a parent(一个旧的事务一定存在父级)");
		this.savepoint = savepoint;
		this.parent = parent;
		this.active = active;
		this.isNew = isNew;
		this.definition = definition;

		/**
		 * 如果当前是一个嵌套事务，或者父级是一个嵌套事务，那么应该受父级管理
		 */
		if (parent != null && (savepoint != null || parent.hasSavepoint())) {
			parent.registerSynchronization(new Synchronization() {

				@Override
				public void beforeCompletion() throws Throwable {
					commit();
				}

				@Override
				public void afterCompletion(Status status) {
					if (status.equals(Status.ROLLED_BACK)) {
						rollback();
					} else if (status.equals(Status.COMPLETED)) {
						close();
					}
				}
			});
		}
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public final void afterCompletion(Status status) {
		this.status = this.status.changeTo(status);
		synchronization.afterCompletion(status);
	}

	@Override
	public void beforeCompletion() throws Throwable {
		this.status = this.status.changeTo(Status.COMMITTING);
		synchronization.beforeCompletion();
	}

	public void registerResource(Object name, Object resource) {
		if (!isNew) {
			parent.registerResource(name, resource);
			return;
		}

		if (resourceMap == null) {
			resourceMap = new HashMap<Object, Object>(4);
		} else if (resourceMap.containsKey(name)) {
			throw new TransactionException("Resource already exists[" + name + "]");
		}

		resourceMap.put(name, resource);
		if (resource instanceof Resource) {
			this.resource = this.resource.and((Resource) resource);
		}
	}

	public void commit() throws Throwable {
		if (status.isCommitting()) {
			return;
		}

		if (rollbackOnly) {
			throw new TransactionException("Transaction is set to rollback only!");
		}

		beforeCompletion();
		resource.commit();
		afterCompletion(Status.COMMITTED);
	}

	public void close() {
		if (status.isCompleted()) {
			return;
		}

		try {
			if (savepoint != null) {
				savepoint.release();
			}
		} finally {
			try {
				resource.close();
			} finally {
				afterCompletion(Status.COMPLETED);
			}
		}
	}

	public Savepoint createSavepoint() throws TransactionException {
		return resource.createSavepoint();
	}

	public TransactionDefinition getDefinition() {
		return definition;
	}

	public Transaction getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	public <T> T getResource(Object name) {
		if (!isNew) {
			return parent.getResource(name);
		}

		return (T) (resourceMap == null ? null : resourceMap.get(name));
	}

	@Override
	public Status getStatus() {
		return status;
	}

	public boolean hasSavepoint() {
		return savepoint != null;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	@Override
	public void registerSynchronization(Synchronization synchronization) throws TransactionException {
		if (!isNew) {
			parent.registerSynchronization(synchronization);
			return;
		}

		this.synchronization = this.synchronization.and(synchronization);
	}

	public void rollback() throws TransactionException {
		if (status.isRolledBack()) {
			return;
		}

		try {
			afterCompletion(Status.ROLLING_BACK);
		} finally {
			try {
				resource.rollback();
			} finally {
				try {
					if (savepoint != null) {
						savepoint.rollback();
					}
				} finally {
					afterCompletion(Status.ROLLED_BACK);
				}
			}
		}
	}

	public void setRollbackOnly() {
		if (this.rollbackOnly) {
			return;
		}

		this.rollbackOnly = true;
		this.status = this.status.changeTo(Status.MARKED_ROLLBACK);
	}
}
