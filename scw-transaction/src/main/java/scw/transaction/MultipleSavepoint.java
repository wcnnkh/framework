package scw.transaction;

public class MultipleSavepoint implements Savepoint {
	private final Iterable<Savepoint> savepoints;

	public MultipleSavepoint(Iterable<Savepoint> savepoints) {
		this.savepoints = savepoints;
	}

	public void rollback() throws TransactionException {
		for (Savepoint savepoint : savepoints) {
			savepoint.rollback();
		}
	}

	public void release() throws TransactionException {
		for (Savepoint savepoint : savepoints) {
			savepoint.release();
		}
	}
}
