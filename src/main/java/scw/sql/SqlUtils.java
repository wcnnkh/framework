package scw.sql;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class SqlUtils {
	public static String getSQLId(Sql sql) {
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
}
