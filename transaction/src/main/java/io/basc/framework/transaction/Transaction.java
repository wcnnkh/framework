package io.basc.framework.transaction;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.function.ParentDiscover;

/**
 * 事务
 * 
 * @author wcnnkh
 *
 */
public interface Transaction extends Resource, RollbackOnly, ParentDiscover<Transaction> {

	TransactionDefinition getDefinition();

	void registerSynchronization(Synchronization synchronization) throws TransactionException;

	@Nullable
	<T> T getResource(Object name);

	void registerResource(Object name, Object resource) throws TransactionException;

	boolean isRollbackOnly();

	void setRollbackOnly() throws TransactionException;

	boolean isNew();

	boolean isActive();

	boolean hasSavepoint();

	Status getStatus();
}
