package scw.transaction.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.transaction.Isolation;
import scw.transaction.NotSupportTransactionException;
import scw.transaction.SavepointManager;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.support.TransactionSynchronization;

class ConnectionTransaction implements SavepointManager, TransactionSynchronization {
	private static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";
	private final ConnectionFactory connectionFactory;
	private final TransactionDefinition transactionDefinition;
	private Connection connection;
	private int savepointCounter;
	private boolean active;
	private LinkedHashMap<String, Sql> sqlMap;

	public ConnectionTransaction(ConnectionFactory connectionFactory, TransactionDefinition transactionDefinition,
			boolean active) {
		this.active = active;
		this.connectionFactory = connectionFactory;
		this.transactionDefinition = transactionDefinition;
	}

	public boolean hasConnection() {
		return connection != null;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = SqlUtils.newProxyConnection(connectionFactory);
			connection.setAutoCommit(!active);

			if (transactionDefinition.isReadOnly()) {
				connection.setReadOnly(transactionDefinition.isReadOnly());
			}

			Isolation isolation = transactionDefinition.getIsolation();
			if (isolation != Isolation.DEFAULT) {
				connection.setTransactionIsolation(isolation.getLevel());
			}
		}
		return connection;
	}

	public void addSql(Sql sql) {
		if (sql == null) {
			return;
		}

		if (sqlMap == null) {
			sqlMap = new LinkedHashMap<String, Sql>(8);
		}

		sqlMap.put(SqlUtils.getSqlId(sql), sql);
	}

	public void setActive(boolean active) {
		if (hasConnection()) {
			try {
				connection.setAutoCommit(!active);
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
		setActive(active);
	}

	public Object createSavepoint() throws TransactionException {
		savepointCounter++;
		try {
			return getConnection().setSavepoint(SAVEPOINT_NAME_PREFIX + savepointCounter);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		if (!(savepoint instanceof Savepoint)) {
			throw new NotSupportTransactionException("not suppert savepoint");
		}
		try {
			getConnection().rollback((Savepoint) savepoint);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public void releaseSavepoint(Object savepoint) throws TransactionException {
		if (!(savepoint instanceof Savepoint)) {
			throw new NotSupportTransactionException("not suppert savepoint");
		}
		try {
			getConnection().releaseSavepoint((Savepoint) savepoint);
		} catch (SQLException e) {
			throw new TransactionException(e);
		}
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void end() throws TransactionException {
		if (hasConnection()) {
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (active) {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				SqlUtils.closeProxyConnection(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void rollback() throws TransactionException {
		if (hasConnection()) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
	}

	public void process() throws TransactionException {
		if (sqlMap != null && !sqlMap.isEmpty()) {
			try {
				connection = getConnection();
				for (Entry<String, Sql> entry : sqlMap.entrySet()) {
					PreparedStatement preparedStatement = SqlUtils.createPreparedStatement(connection,
							entry.getValue());
					try {
						preparedStatement.execute();
					} catch (SQLException e) {
						throw new TransactionException(SqlUtils.getSqlId(entry.getValue()), e);
					} finally {
						try {
							preparedStatement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (SQLException e) {
				throw new TransactionException(e);
			}
		}
	}
}
