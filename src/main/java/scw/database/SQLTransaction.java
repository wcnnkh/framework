package scw.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.exception.TransactionProcessException;
import scw.common.utils.XUtils;
import scw.jdbc.Sql;

public final class SQLTransaction extends AbstractTransaction {
	private Map<String, Sql> sqlMap = new HashMap<String, Sql>(4, 1);
	private Connection connection;
	private ConnectionSource connectionSource;
	private PreparedStatement[] preparedStatements;

	public SQLTransaction(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

	public SQLTransaction(Connection connection) {
		this.connection = connection;
	}

	public void addSql(Sql sql) {
		if (sql == null) {
			return;
		}

		String id = DataBaseUtils.getSQLId(sql);
		sqlMap.put(id, sql);
	}

	public void begin() throws Exception {
		if (!sqlMap.isEmpty()) {
			if (connection == null) {
				connection = connectionSource.getConnection();
			}

			connection.setAutoCommit(false);
			preparedStatements = new PreparedStatement[sqlMap.size()];
		}
	}

	public void process() throws Exception {
		int i = 0;
		for (Entry<String, Sql> entry : sqlMap.entrySet()) {
			PreparedStatement stmt = DataBaseUtils.createPreparedStatement(connection, entry.getValue());
			preparedStatements[i++] = stmt;
			try {
				stmt.execute();
			} catch (SQLException e) {
				throw new TransactionProcessException(DataBaseUtils.getSQLId(entry.getValue()), e);
			}
		}
	}

	public void end() throws Exception {
		if (preparedStatements != null) {
			XUtils.close(preparedStatements);
		}

		if (connection != null) {
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void rollback() throws Exception {
		if (connection != null) {
			connection.rollback();
		}
	}
}