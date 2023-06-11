package io.basc.framework.context.transaction;

import io.basc.framework.tx.Isolation;
import io.basc.framework.tx.Propagation;
import io.basc.framework.tx.TransactionDefinition;


final class AnnotationTransactionDefinition implements TransactionDefinition {
	private final Transactional tx;

	public AnnotationTransactionDefinition(Transactional tx) {
		this.tx = tx;
	}

	public Propagation getPropagation() {
		return tx == null ? Propagation.REQUIRED : tx.propagation();
	}

	public Isolation getIsolation() {
		return tx == null ? Isolation.DEFAULT : tx.isolation();
	}

	public int getTimeout() {
		return tx == null ? -1 : tx.timeout();
	}

	public boolean isReadOnly() {
		return tx == null ? false : tx.readOnly();
	}
}
