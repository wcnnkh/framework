package io.basc.framework.transaction.aop;

import io.basc.framework.execution.Function;
import io.basc.framework.lang.Nullable;
import io.basc.framework.transaction.TransactionDefinition;

public interface TransactionDefinitionLoader {
	@Nullable
	TransactionDefinition load(Function function);

	default boolean isRollback(Function function, Throwable error) {
		return true;
	}
}
