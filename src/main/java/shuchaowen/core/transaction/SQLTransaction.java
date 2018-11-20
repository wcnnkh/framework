package shuchaowen.core.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.ConnectionPool;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.util.XUtils;

public final class SQLTransaction extends AbstractTransaction {
	private Map<String, SQL> sqlMap = new HashMap<String, SQL>(4, 1);
	private Connection connection;
	private ConnectionPool connectionPool;
	private PreparedStatement[] preparedStatements;
	private int transactionLevel = -1;
	private int oldTransactionLevel = -1;

	public SQLTransaction(ConnectionPool connectionPool) {
		this(connectionPool, -1);
	}
	
	public SQLTransaction(Connection connection){
		this(connection, -1);
	}
	
	public SQLTransaction(Connection connection, int transactionLevel) {
		this.connection = connection;
		this.transactionLevel = transactionLevel;
	}
	
	/**
	 * @param db
	 * @param transactionLevel
	 * @param updateStack
	 *            如果为true 那么执行后的updateCount大于0就成功，不然就抛出异常.
	 */
	public SQLTransaction(ConnectionPool connectionPool, int transactionLevel) {
		this.connectionPool = connectionPool;
		this.transactionLevel = transactionLevel;
	}

	public void clear() {
		connection = null;
		preparedStatements = null;
		sqlMap.clear();
	}

	public void addSql(SQL sql) {
		if (sql == null) {
			return;
		}
		
		String id = DBUtils.getSQLId(sql);
		sqlMap.put(id, sql);
	}

	public void begin() throws Exception {
		if (!sqlMap.isEmpty()) {
			if(connection == null){
				connection = connectionPool.getConnection();
			}
			
			connection.setAutoCommit(false);
			this.oldTransactionLevel = connection.getTransactionIsolation();
			if (transactionLevel >= 0) {
				connection.setTransactionIsolation(transactionLevel);
			}

			preparedStatements = new PreparedStatement[sqlMap.size()];
		}
	}

	public void process() throws Exception {
		if (!sqlMap.isEmpty()) {
			int i = 0;
			for (Entry<String, SQL> entry : sqlMap.entrySet()) {
				PreparedStatement stmt = connection.prepareStatement(entry.getValue().getSql());
				preparedStatements[i++] = stmt;
				DBUtils.setParams(stmt, entry.getValue().getParams());
				try {
					stmt.execute();
				} catch (SQLException e) {
					throw new Error(DBUtils.getSQLId(entry.getValue()), e);
				}
			}
		}
	}

	public void end() throws Exception {
		if (!sqlMap.isEmpty()) {
			if (preparedStatements != null) {
				XUtils.close(true, preparedStatements);
			}

			if (connection != null) {
				connection.commit();
				if (transactionLevel >= 0) {
					connection.setTransactionIsolation(oldTransactionLevel);
				}
				connection.setAutoCommit(true);
				connection.close();
			}
			connection = null;
		}
	}

	public void rollback() throws Exception {
		if (connection != null) {
			connection.rollback();
		}
	}
}