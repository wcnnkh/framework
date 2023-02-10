package io.basc.framework.transaction;

import io.basc.framework.util.ConsumeProcessor;

public class MultipleSavepoint implements Savepoint {
	private final Iterable<Savepoint> savepoints;

	public MultipleSavepoint(Iterable<Savepoint> savepoints) {
		this.savepoints = savepoints;
	}

	public void rollback() throws TransactionException {
		ConsumeProcessor.consumeAll(savepoints, (e) -> e.rollback());
	}

	public void release() throws TransactionException {
		ConsumeProcessor.consumeAll(savepoints, (e) -> e.release());
	}
}
