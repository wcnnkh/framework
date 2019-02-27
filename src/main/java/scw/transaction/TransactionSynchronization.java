package scw.transaction;

public interface TransactionSynchronization {

	void process();

	void rollback();

	void end();
}
