package scw.transaction;

public interface TransactionSynchronization {

	void process() throws Throwable;

	void rollback();

	void end();
}
