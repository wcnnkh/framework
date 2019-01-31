package scw.transaction.tcc;

import scw.transaction.Transaction;

public class TccTransaction implements Transaction {
	private boolean completed;

	public boolean isCompleted() {
		return completed;
	}

}
