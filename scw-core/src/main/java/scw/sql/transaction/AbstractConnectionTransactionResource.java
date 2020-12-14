package scw.sql.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;

import scw.core.utils.CollectionUtils;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.transaction.Savepoint;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;

public abstract class AbstractConnectionTransactionResource implements TransactionResource {
	private static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";
	private final TransactionDefinition transactionDefinition;
	private final boolean active;
	private int savepointCounter;
	private LinkedHashSet<Sql> sqls;

	public AbstractConnectionTransactionResource(TransactionDefinition transactionDefinition, boolean active) {
		this.transactionDefinition = transactionDefinition;
		this.active = active;
	}

	public abstract boolean hasConnection();

	public abstract Connection getConnection() throws SQLException;

	public TransactionDefinition getTransactionDefinition() {
		return transactionDefinition;
	}

	public boolean addSql(Sql sql) {
		if (sql == null) {
			return false;
		}

		if (sqls == null) {
			sqls = new LinkedHashSet<Sql>();
		}

		return sqls.add(sql);
	}

	public boolean isActive() {
		return active;
	}

	public Savepoint createSavepoint() throws TransactionException {
		savepointCounter++;
		try {
			return new ConnectionSavepoint(getConnection(), SAVEPOINT_NAME_PREFIX + savepointCounter);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void commit() throws Throwable {
		if(!CollectionUtils.isEmpty(sqls)){
			for(Sql sql : sqls){
				SqlUtils.execute(getConnection(), sql);
			}
		}

		if (hasConnection()) {
			Connection connection = getConnection();
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
		}
	}
}
