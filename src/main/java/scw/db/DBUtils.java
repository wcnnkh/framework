package scw.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.XUtils;
import scw.db.sql.SQL;
import scw.db.sql.SQLFormat;
import scw.db.transaction.SQLTransaction;

public final class DBUtils {
	private DBUtils() {
	};

	public static void setParams(PreparedStatement preparedStatement,
			Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}

	public static void iterator(ConnectionSource connectionSource, SQL sql,
			scw.common.Iterator<ResultSet> iterator) {
		if (sql == null || connectionSource == null || iterator == null) {
			return;
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			rs = stmt.executeQuery();

			while (rs.next()) {
				iterator.iterator(rs);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(getSQLId(sql), e);
		} finally {
			XUtils.close(rs, stmt, connection);
		}
	}

	public static String getSQLId(SQL sql) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(sql.getSql());
		sb.append("]");
		sb.append(" - ");
		sb.append(sql.getParams() == null ? "[]" : Arrays.toString(sql
				.getParams()));
		return sb.toString();
	}

	public static void execute(ConnectionSource connectionPool,
			Collection<SQL> sqls) {
		if (sqls == null || connectionPool == null) {
			throw new NullPointerException();
		}

		Iterator<SQL> iterator = sqls.iterator();
		if (sqls.size() == 1) {
			SQL sql = iterator.next();
			PreparedStatement stmt = null;
			Connection connection = null;
			try {
				connection = connectionPool.getConnection();
				stmt = connection.prepareStatement(sql.getSql());
				setParams(stmt, sql.getParams());
				stmt.execute();
			} catch (SQLException e) {
				throw new ShuChaoWenRuntimeException(DBUtils.getSQLId(sql), e);
			} finally {
				XUtils.close(stmt, connection);
			}
		} else {
			SQLTransaction sqlTransaction = new SQLTransaction(connectionPool);
			while (iterator.hasNext()) {
				sqlTransaction.addSql(iterator.next());
			}
			sqlTransaction.execute();
		}
	}

	public static scw.db.result.ResultSet select(
			ConnectionSource connectionSource, SQL sql) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			rs = stmt.executeQuery();
			return new scw.db.result.ResultSet(rs);
		} catch (SQLException e) {
			throw new ShuChaoWenRuntimeException(getSQLId(sql), e);
		} finally {
			XUtils.close(rs, stmt, connection);
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

	public static Collection<SQL> getSqlList(SQLFormat sqlFormat,
			Collection<OperationBean> operationBeans) {
		if (operationBeans == null || operationBeans.isEmpty()) {
			return null;
		}

		List<SQL> list = new ArrayList<SQL>();
		for (OperationBean operationBean : operationBeans) {
			if (operationBean == null) {
				continue;
			}

			list.add(operationBean.getSql(sqlFormat));
		}
		return list;
	}

	public static List<SQL> getSaveSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}

			sqls.add(sqlFormat.toInsertSql(obj));
		}
		return sqls;
	}

	public static List<SQL> getUpdateSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(sqlFormat.toUpdateSql(obj));
		}
		return sqls;
	}

	public static List<SQL> getDeleteSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(sqlFormat.toDeleteSql(obj));
		}
		return sqls;
	}

	public static List<SQL> getSaveOrUpdateSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(sqlFormat.toSaveOrUpdateSql(obj));
		}
		return sqls;
	}
}
