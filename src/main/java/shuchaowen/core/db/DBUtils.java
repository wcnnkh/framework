package shuchaowen.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.transaction.SQLTransaction;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.XUtils;

public final class DBUtils {
	public static void setParams(PreparedStatement preparedStatement,
			Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}
	
	public static void iterator(AbstractDB db, SQL sql, ResultIterator iterator){
		iterator(db, sql, null, iterator);
	}
	
	public static void iterator(AbstractDB db, SQL sql, TableMapping tableMapping, ResultIterator iterator){
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = db.getConnection();
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			rs = stmt.executeQuery();
			while(rs.next()){
				iterator.next(new Result(tableMapping, rs));
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(sql.getSql(), e);
		} finally {
			XUtils.close(true, rs, stmt, connection);
		}
	}

	public static String getSQLId(String sql, Object[] args) {
		StringBuilder sb = new StringBuilder(1024);
		if (args != null) {
			sb.append(args.length);
			sb.append("#");

			StringBuilder paramSb = new StringBuilder(128);
			paramSb.append("[");
			for (Object obj : args) {
				String str = String.valueOf(obj);
				paramSb.append(str);
				paramSb.append(",");
				if (obj == null) {
					sb.append(-1);
				} else {
					sb.append(str.length());
				}
				sb.append("#");
			}
			paramSb.append("]");
			sb.append(paramSb.toString());
		}
		sb.append(sql);
		return sb.toString();
	}

	public static String getSQLId(SQL sql) {
		return getSQLId(sql.getSql(), sql.getParams());
	}

	public static void execute(ConnectionPool connectionPool, SQL sql) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = connectionPool.getConnection();
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			stmt.execute();
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(sql.getSql(), e);
		} finally {
			XUtils.close(true, stmt, connection);
		}
	}

	public static void execute(ConnectionPool connectionPool,
			Collection<SQL> sqls) {
		if (sqls == null || connectionPool== null) {
			throw new NullPointerException();
		}

		Iterator<SQL> iterator = sqls.iterator();
		if (sqls.size() == 1) {
			SQL sql = iterator.next();
			execute(connectionPool, sql);
		} else {
			SQLTransaction sqlTransaction = new SQLTransaction(connectionPool);
			while (iterator.hasNext()) {
				sqlTransaction.addSql(iterator.next());
			}
			try {
				sqlTransaction.execute();
			} catch (Throwable e) {
				throw new ShuChaoWenRuntimeException(e);
			}
		}
	}
	
	public static shuchaowen.core.db.result.ResultSet select(AbstractDB db, SQL sql){
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = db.getConnection();
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			rs = stmt.executeQuery();
			return new shuchaowen.core.db.result.ResultSet(rs);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(sql.getSql(), e);
		} finally {
			XUtils.close(true, rs, stmt, connection);
		}
	}

	public static String getTableAndColumn(String tableName, String columnName) {
		StringBuilder sb = new StringBuilder(32);
		sb.append("`");
		sb.append(tableName);
		sb.append("`.`");
		sb.append(columnName);
		sb.append("`");
		return sb.toString();
	}

	public static String getTableName(Object obj) {
		String tableName;
		if (obj instanceof TableName) {
			tableName = ((TableName) obj).tableName();
		} else {
			TableInfo tableInfo = DB.getTableInfo(obj.getClass());
			tableName = tableInfo.getName();
		}
		return tableName;
	}
}
