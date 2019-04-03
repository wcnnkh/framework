package scw.db.async;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.sql.orm.SqlFormat;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public class AsyncInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Collection<Sql> sqls;
	private MultipleOperation multipleOperation;

	protected AsyncInfo() {
	}

	public AsyncInfo(Collection<Sql> sqls) {
		this.sqls = sqls;
	}

	public AsyncInfo(MultipleOperation multipleOperation) {
		this.multipleOperation = multipleOperation;
	}

	public void execute(SqlOperations sqlOperations, SqlFormat sqlFormat) throws Throwable {
		Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			if (sqls != null) {
				for (Sql sql : sqls) {
					if (sql == null) {
						continue;
					}

					sqlOperations.execute(sql);
				}
			}

			if (multipleOperation != null) {
				List<Sql> list = multipleOperation.format(sqlFormat);
				if (list != null) {
					for (Sql sql : list) {
						if (sql == null) {
							continue;
						}

						sqlOperations.execute(sql);
					}
				}
			}

			TransactionManager.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			throw e;
		}
	}
}
