package io.basc.framework.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Registration;

public class DefaultTransaction implements Transaction, Synchronization {
	private final boolean active;
	private final TransactionDefinition definition;
	private final boolean isNew;
	private final Transaction parent;
	private Map<Object, Object> resourceMap;
	private boolean rollbackOnly;// 标记此事务直接回滚
	private Savepoint savepoint;
	private TransactionStatus status = TransactionStatus.UNKNOWN;
	private List<Synchronization> synchronizations;

	/**
	 * 创建一个新的事务
	 * 
	 */
	public DefaultTransaction(Transaction parent, TransactionDefinition definition, boolean active) {
		this(parent, definition, active, true, null);
	}

	/**
	 * 包装旧的事务
	 * 
	 */
	public DefaultTransaction(Transaction parent, TransactionDefinition definition) {
		this(parent, definition, parent.isActive(), false, null);
	}

	/**
	 * 嵌套事务
	 * 
	 */
	public DefaultTransaction(Transaction parent, TransactionDefinition definition, Savepoint savepoint,
			boolean isNew) {
		this(parent, definition, parent.isActive(), isNew, savepoint);
	}

	/**
	 * 自定义
	 * 
	 * @param parent
	 * @param definition
	 * @param active
	 * @param isNew
	 * @param savepoint
	 */
	public DefaultTransaction(Transaction parent, TransactionDefinition definition, boolean active, boolean isNew,
			@Nullable Savepoint savepoint) {
		Assert.isTrue(!(!isNew && parent == null), "An old transaction must have a parent(一个旧的事务一定存在父级)");
		this.savepoint = savepoint;
		this.parent = parent;
		this.isNew = isNew;
		this.active = active;
		this.definition = definition;

		/**
		 * 如果当前是一个嵌套事务，或者父级是一个嵌套事务，那么应该受父级管理
		 */
		if (parent != null && (savepoint != null || parent.hasSavepoint())) {
			parent.registerSynchronization(this);
		}
	}

	@Override
	public final void afterCompletion(TransactionStatus status) {
		this.status = this.status.changeTo(status);
		if (synchronizations != null) {
			Iterator<Synchronization> iterator = CollectionUtils.getIterator(synchronizations,
					this.status.isCompleted());
			ConsumeProcessor.consumeAll(iterator, (s) -> s.afterCompletion(this.status));
		}
	}

	@Override
	public void beforeCompletion() throws Throwable {
		if (isCompleted()) {
			return;
		}

		if (isRollbackOnly()) {
			return;
		}

		if (!isNew) {
			return;
		}

		try {
			afterCompletion(TransactionStatus.COMMITTING);
			if (synchronizations != null) {
				for (Synchronization synchronization : synchronizations) {
					synchronization.beforeCompletion();
				}
			}
		} finally {
			afterCompletion(TransactionStatus.COMMITTED);
		}
	}

	public Registration registerResource(Object name, Object resource) {
		if (!isNew) {
			return parent.registerResource(name, resource);
		}

		if (resourceMap == null) {
			resourceMap = new HashMap<Object, Object>(4);
		} else if (resourceMap.containsKey(name)) {
			throw new TransactionException("Resource already exists[" + name + "]");
		}

		resourceMap.put(name, resource);
		Registration registration = () -> resourceMap.remove(name);
		if (resource instanceof Synchronization) {
			registration = registration.and(registerSynchronization((Synchronization) resource));
		}
		return registration;
	}

	public void commit() throws Throwable {
		if (isCompleted()) {
			return;
		}

		if (rollbackOnly) {
			throw new TransactionException("Transaction is set to rollback only!");
		}

		try {
			beforeCompletion();
		} finally {
			complete();
		}
	}

	public void complete() {
		try {
			if (savepoint != null) {
				savepoint.release();
			}
		} finally {
			afterCompletion(TransactionStatus.COMPLETED);
		}
	}

	public Savepoint createSavepoint() throws TransactionException {
		if (resourceMap == null) {
			return Savepoint.EMPTY;
		}

		List<Savepoint> savepoints = new ArrayList<Savepoint>(resourceMap.size());
		for (Entry<Object, Object> entry : resourceMap.entrySet()) {
			Object resource = entry.getValue();
			if (resource instanceof SavepointManager) {
				savepoints.add(((SavepointManager) resource).createSavepoint());
			}
		}

		if (savepoints.isEmpty()) {
			return Savepoint.EMPTY;
		}

		return new MultipleSavepoint(savepoints);
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
	public TransactionStatus getStatus() {
		return status;
	}

	public boolean hasSavepoint() {
		return savepoint != null;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public boolean isCompleted() {
		return status.isCompleted();
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	@Override
	public Registration registerSynchronization(Synchronization synchronization) throws TransactionException {
		if (!isNew) {
			return parent.registerSynchronization(synchronization);
		}

		if (synchronizations == null) {
			synchronizations = new ArrayList<Synchronization>(8);
		} else if (synchronizations.contains(synchronization)) {
			throw new TransactionException("This transaction synchronization[" + synchronization + "] already exists");
		}

		synchronizations.add(synchronization);
		return () -> synchronizations.remove(synchronization);
	}

	public void rollback() throws TransactionException {
		if (isCompleted()) {
			return;
		}

		try {
			afterCompletion(TransactionStatus.ROLLING_BACK);
		} finally {
			try {
				if (savepoint != null) {
					savepoint.rollback();
				}
			} finally {
				try {
					afterCompletion(TransactionStatus.ROLLED_BACK);
				} finally {
					complete();
				}
			}
		}
	}

	public void setRollbackOnly() {
		if (this.rollbackOnly) {
			return;
		}

		this.rollbackOnly = true;
		this.status = this.status.changeTo(TransactionStatus.MARKED_ROLLBACK);
	}
}
