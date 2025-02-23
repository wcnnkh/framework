package io.basc.framework.transaction.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.transaction.TransactionDefinition;

public interface TransactionDefinitionLoader {
	TransactionDefinition load(Function function);

	default boolean isRollback(Function function, Throwable error) {
		return true;
	}
}
