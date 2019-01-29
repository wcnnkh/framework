package scw.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scw.beans.BeanFieldListen;
import scw.beans.BeanUtils;
import scw.common.Logger;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.common.utils.XUtils;
import scw.database.annoation.Table;

public final class DataBaseUtils {
	private DataBaseUtils() {
	};

	private volatile static Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();

	public static TableInfo getTableInfo(Class<?> clz) {
		return getTableInfo(clz.getName());
	}

	private static TableInfo getTableInfo(String className) {
		String name = ClassUtils.getProxyRealClassName(className);
		TableInfo tableInfo = tableMap.get(name);
		if (tableInfo == null) {
			synchronized (tableMap) {
				tableInfo = tableMap.get(name);
				if (tableInfo == null) {
					tableInfo = new TableInfo(ClassUtils.getClassInfo(name));
					tableMap.put(name, tableInfo);
				}
			}
		}
		return tableInfo;
	}

	private static void setParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}

	public static PreparedStatement createPreparedStatement(Connection connection, SQL sql) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql());
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql());
		}

		setParams(preparedStatement, sql.getParams());
		return preparedStatement;
	}

	public static PreparedStatement createPreparedStatement(Connection connection, SQL sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(), resultSetType, resultSetConcurrency);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(), resultSetType, resultSetConcurrency);
		}

		setParams(preparedStatement, sql.getParams());
		return preparedStatement;
	}

	public static PreparedStatement createPreparedStatement(Connection connection, SQL sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql(), resultSetType, resultSetConcurrency,
					resultSetHoldability);
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql(), resultSetType, resultSetConcurrency,
					resultSetHoldability);
		}

		setParams(preparedStatement, sql.getParams());
		return preparedStatement;
	}

	public static void iterator(ConnectionSource connectionSource, SQL sql, scw.common.Iterator<ResultSet> iterator) {
		if (sql == null || connectionSource == null || iterator == null) {
			return;
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			stmt = createPreparedStatement(connection, sql);
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

	public static scw.database.ResultSet select(ConnectionSource connectionSource, SQL sql) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			stmt = createPreparedStatement(connection, sql);
			rs = stmt.executeQuery();
			return new DefaultResultSet(rs);
		} catch (SQLException e) {
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
		sb.append(sql.getParams() == null ? "[]" : Arrays.toString(sql.getParams()));
		return sb.toString();
	}

	public static void execute(ConnectionSource connectionPool, Collection<SQL> sqls) {
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
				stmt = createPreparedStatement(connection, sql);
				setParams(stmt, sql.getParams());
				stmt.execute();
			} catch (SQLException e) {
				throw new ShuChaoWenRuntimeException(getSQLId(sql), e);
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

	public static String getLikeValue(String likeValue) {
		if (likeValue == null || likeValue.length() == 0) {
			return "%";// 注意：这会忽略空
		}

		return "%" + likeValue + "%";
	}

	public static void registerCglibProxyTableBean(String pageName) {
		Logger.info(DataBaseUtils.class.getName(), "register proxy package:" + pageName);
		for (Class<?> type : ClassUtils.getClasses(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (BeanFieldListen.class.isAssignableFrom(type)) {
				continue;
			}

			BeanUtils.getFieldListenProxyClass(type);
		}
	}

	/**
	 * 将数据库值转化java类型
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static Object parse(Class<?> type, Object value) {
		if (value == null) {
			return value;
		}

		if (ClassUtils.isBooleanType(type)) {
			if (value != null) {
				if (value instanceof Number) {
					return ((Number) value).doubleValue() == 1;
				} else if (value instanceof String) {
					return StringUtils.parseBoolean((String) value);
				}
			}
		} else if (ClassUtils.isIntType(type)) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			}
		} else if (ClassUtils.isLongType(type)) {
			if (value instanceof Number) {
				return ((Number) value).longValue();
			}
		} else if (ClassUtils.isByteType(type)) {
			if (value instanceof Number) {
				return ((Number) value).byteValue();
			}
		} else if (ClassUtils.isFloatType(type)) {
			if (value instanceof Number) {
				return ((Number) value).floatValue();
			}
		} else if (ClassUtils.isDoubleType(type)) {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
			}
		} else if (ClassUtils.isShortType(type)) {
			if (value instanceof Number) {
				return ((Number) value).shortValue();
			}
		}
		return value;
	}
}
