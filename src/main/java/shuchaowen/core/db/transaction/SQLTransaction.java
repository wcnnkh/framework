package shuchaowen.core.db.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.ConnectionOrigin;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.util.XUtils;

public class SQLTransaction extends Transaction {
	private Map<String, SQL> sqlMap = new HashMap<String, SQL>();
	private Connection connection;
	private ConnectionOrigin db;
	private PreparedStatement[] preparedStatements;
	private int transactionLevel = -1;
	private int oldTransactionLevel = -1;

	public SQLTransaction(ConnectionOrigin db) {
		this(db, -1);
	}

	/**
	 * @param db
	 * @param transactionLevel
	 * @param updateStack
	 *            如果为true 那么执行后的updateCount大于0就成功，不然就抛出异常.
	 */
	public SQLTransaction(ConnectionOrigin db, int transactionLevel) {
		this.db = db;
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

	@Override
	public void begin() throws Exception {
		if (!sqlMap.isEmpty()) {
			connection = db.getConnection();
			connection.setAutoCommit(false);
			this.oldTransactionLevel = connection.getTransactionIsolation();
			if (transactionLevel >= 0) {
				connection.setTransactionIsolation(transactionLevel);
			}

			preparedStatements = new PreparedStatement[sqlMap.size()];
		}
	}

	@Override
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
					throw new SQLException(entry.getValue().getSql(), e);
				}
			}
		}
	}

	@Override
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

	@Override
	public void rollback() throws Exception {
		if (connection != null) {
			connection.rollback();
		}
	}
}