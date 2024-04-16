package io.basc.framework.autoconfigure.transaction;

import io.basc.framework.execution.Function;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.aop.TransactionDefinitionLoader;

public class AnnotationTransactionDefinitionLoader implements TransactionDefinitionLoader {

	@Override
	public TransactionDefinition load(Function function) {
		Transactional transactional = function.getAnnotations().get(Transactional.class).synthesize();
		if (transactional == null) {
			return null;
		}
		return new AnnotationTransactionDefinition(transactional);
	}

	@Override
	public boolean isRollback(Function function, Throwable error) {
		Transactional transactional = function.getAnnotations().get(Transactional.class).synthesize();
		if (transactional == null) {
			return true;
		}

		return transactional.rollbackFor().isAssignableFrom(error.getClass());
	}

}
