package scw.sql.transaction;

public class DefaultTransactionDefinition implements TransactionDefinition {

	public Propagation getPropagation() {
		return Propagation.REQUIRED;
	}

	public Isolation getIsolation() {
		return Isolation.DEFAULT;
	}

	public int getTimeout() {
		return -1;
	}

	public boolean isReadOnly() {
		return false;
	}

}
