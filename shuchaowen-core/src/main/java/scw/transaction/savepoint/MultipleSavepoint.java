package scw.transaction.savepoint;

import java.util.Collection;
import java.util.LinkedList;

import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;

public class MultipleSavepoint extends LinkedList<Savepoint> implements Savepoint {
	private static final long serialVersionUID = 1L;

	public MultipleSavepoint(Collection<? extends TransactionResource> resources) {
		if (resources != null) {
			for (TransactionResource tr : resources) {
				Savepoint savepoint = tr.createSavepoint();
				if (savepoint == null) {
					continue;
				}

				add(savepoint);
			}
		}
	}

	public void rollback() throws TransactionException {
		for (Savepoint savepoint : this) {
			savepoint.rollback();
		}
	}

	public void release() throws TransactionException {
		for (Savepoint savepoint : this) {
			savepoint.release();
		}
	}
}
