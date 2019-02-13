package scw.transaction.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import scw.common.utils.CollectionUtils;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.transaction.TransactionException;
import scw.transaction.synchronization.TransactionSynchronization;

public class ConnectionTransactionSynchronization implements TransactionSynchronization {
	private final ConnectionTransaction connectionTransaction;
	private LinkedHashMap<String, Sql> sqlMap;

	public ConnectionTransactionSynchronization(ConnectionTransaction connectionTransaction) {
		this.connectionTransaction = connectionTransaction;
	}

	public void addSql(Sql... sqls) {
		if (sqlMap == null) {
			sqlMap = new LinkedHashMap<String, Sql>(4, 1);
		}

		for (Sql sql : sqls) {
			if (sql == null) {
				continue;
			}

			sqlMap.put(SqlUtils.getSqlId(sql), sql);
		}
	}

	public void addSql(Collection<Sql> sqls) {
		if (CollectionUtils.isEmpty(sqls)) {
			return;
		}

		if (sqlMap == null) {
			sqlMap = new LinkedHashMap<String, Sql>(4, 1);
		}

		Iterator<Sql> iterator = sqls.iterator();
		while (iterator.hasNext()) {
			Sql sql = iterator.next();
			if (sql == null) {
				continue;
			}

			sqlMap.put(SqlUtils.getSqlId(sql), sql);
		}
	}

	public void begin() throws TransactionException {
		if (connectionTransaction != null) {
			connectionTransaction.begin();

			Connection connection;
			try {
				connection = connectionTransaction.getConnection();
			} catch (SQLException e) {
				throw new TransactionException(e);
			}

			if (sqlMap != null) {
				for (Entry<String, Sql> entry : sqlMap.entrySet()) {
					PreparedStatement preparedStatement = null;
					try {
						preparedStatement = SqlUtils.createPreparedStatement(connection, entry.getValue());
					} catch (SQLException e) {
						throw new TransactionException(e);
					} finally {
						if (preparedStatement != null) {
							try {
								preparedStatement.close();
							} catch (SQLException e) {
								throw new TransactionException(e);
							}
						}
					}
				}
			}
		}
	}

	public void commit() throws TransactionException {
		if (connectionTransaction != null) {
			connectionTransaction.commit();
		}
	}

	public void rollback() throws TransactionException {
		if (connectionTransaction != null) {
			connectionTransaction.rollback();
		}
	}

	public void end() {
		if (connectionTransaction != null) {
			connectionTransaction.end();
		}
	}
}
