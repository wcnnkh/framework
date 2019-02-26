package scw.transaction;

interface TransactionSynchronization {

	void process();

	void rollback();

	void end();
}
