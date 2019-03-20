package scw.sql;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public class Sqls extends LinkedList<Sql> implements Serializable {
	private static final long serialVersionUID = 1L;

	public Sqls() {
	};

	public Sqls(Sql... sqls) {
		for (Sql sql : sqls) {
			add(sql);
		}
	}

	public Sqls(Collection<Sql> sqls) {
		super(sqls);
	}

	public void execute(SqlOperations sqlOperations) {
		if (isEmpty()) {
			return;
		}

		for (Sql sql : this) {
			sqlOperations.execute(sql);
		}
	}

	public void transactionExecute(SqlOperations sqlOperations) {
		if (isEmpty()) {
			return;
		}

		Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			for (Sql sql : this) {
				if (sql == null) {
					continue;
				}

				sqlOperations.execute(sql);
			}

			TransactionManager.commit(transaction);
		} catch (Exception e) {
			TransactionManager.rollback(transaction);
			e.printStackTrace();
		}
	}
}
