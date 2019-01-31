package scw.transaction.tcc;

import scw.transaction.Transaction;

public class TccTransactionContext implements Transaction {
	private TccTransactionCollection tccTransactionCollection;
	private TccTransactionContext parent;

	public TccTransactionContext(TccTransactionContext parent) {
		this.parent = parent;
	}

	public void addTransaction(TccTransactionItem transaction) {
		if (tccTransactionCollection == null) {
			tccTransactionCollection = new TccTransactionCollection();
		}
		tccTransactionCollection.add(tccTransactionCollection);
	}
	
	public void complete() {
		if (tccTransactionCollection == null) {
			try {
				tccTransactionCollection.begin();
				tccTransactionCollection.process();
			} catch (Exception e) {
				tccTransactionCollection.rollback();
			} finally {
				tccTransactionCollection.end();
				tccTransactionCollection = null;
			}
		}
	}
	
	public boolean isCompleted() {
		return tccTransactionCollection == null;
	}
}
