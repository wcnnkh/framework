package scw.sql;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import scw.sql.transaction.ConnectionProxy;
import scw.sql.transaction.UnableToCloseConnectionProxyHandler;

public abstract class SqlUtils {
	public static String getSqlId(Sql sql) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(sql.getSql());
		sb.append("]");
		sb.append(" - ");
		sb.append(sql.getParams() == null ? "[]" : Arrays.toString(sql.getParams()));
		return sb.toString();
	}

	/**
	 * 创建一个代理连接
	 * 
	 * @param connectionFactory
	 * @return
	 * @throws SQLException
	 */
	public static ConnectionProxy newProxyConnection(ConnectionFactory connectionFactory) throws SQLException {
		Connection connection = connectionFactory.getConnection();
		return (ConnectionProxy) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(),
				new Class<?>[] { ConnectionProxy.class }, new UnableToCloseConnectionProxyHandler(connection));
	}

	/**
	 * 关闭一个代理连接，如果这不是一个代理连接就直接关闭
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void closeProxyConnection(Connection connection) throws SQLException {
		if (connection instanceof ConnectionProxy) {
			((ConnectionProxy) connection).getTargetConnection().close();
		} else {
			connection.close();
		}
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql) throws SQLException {
		PreparedStatement statement;
		if (sql.isStoredProcedure()) {
			statement = connection.prepareCall(sql.getSql());
		} else {
			statement = connection.prepareStatement(sql.getSql());
		}

		setSqlParams(statement, sql.getParams());
		return statement;
	}

	public static void setSqlParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(), resultSetType, resultSetConcurrency);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(), resultSetType, resultSetConcurrency);
		}

		setSqlParams(preparedStatement, sql.getParams());
		return preparedStatement;
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(), resultSetType, resultSetConcurrency,
					resultSetHoldability);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(), resultSetType, resultSetConcurrency,
					resultSetHoldability);
		}

		setSqlParams(preparedStatement, sql.getParams());
		return preparedStatement;
	}

}
