package scw.sql;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.io.ResourceUtils;
import scw.sql.orm.ObjectRelationalMapping;

public final class SqlUtils {
	private static final String IGNORE_SQL_START_WITH = StringUtils
			.toString(GlobalPropertyFactory.getInstance().getString("db.file.sql.ignore.start.with"), "##");
	private static final ObjectRelationalMapping OBJECT_RELATIONAL_MAPPING = InstanceUtils
			.getSystemConfiguration(ObjectRelationalMapping.class);

	private SqlUtils() {
	};

	public static ObjectRelationalMapping getObjectRelationalMapping() {
		return OBJECT_RELATIONAL_MAPPING;
	}

	public static String getSqlId(Sql sql) {
		Object[] params = sql.getParams();
		if (params == null || params.length == 0) {
			return sql.getSql();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(sql.getSql());
			sb.append("]");
			sb.append(" - ");
			sb.append(Arrays.toString(params));
			return sb.toString();
		}
	}

	public static PreparedStatement createPreparedStatement(Connection connection, Sql sql) throws SQLException {
		PreparedStatement statement;
		if (sql.isStoredProcedure()) {
			statement = connection.prepareCall(sql.getSql());
		} else {
			statement = connection.prepareStatement(sql.getSql());
		}

		try {
			setSqlParams(statement, sql.getParams());
		} catch (SQLException e) {
			statement.close();
			throw e;
		}
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

		try {
			setSqlParams(preparedStatement, sql.getParams());
		} catch (SQLException e) {
			preparedStatement.close();
			throw e;
		}
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

		try {
			setSqlParams(preparedStatement, sql.getParams());
		} catch (SQLException e) {
			preparedStatement.close();
			throw e;
		}
		return preparedStatement;
	}

	public static void query(Connection connection, Sql sql, ResultSetCallback resultSetCallback) throws SQLException {

		PreparedStatement statement = null;
		try {
			statement = createPreparedStatement(connection, sql);
			query(statement, resultSetCallback);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static void query(PreparedStatement statement, ResultSetCallback resultSetCallback) throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = statement.executeQuery();
			resultSetCallback.process(resultSet);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	public static <T> T query(Connection connection, Sql sql, ResultSetMapper<T> resultSetMapper) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = SqlUtils.createPreparedStatement(connection, sql);
			return query(statement, resultSetMapper);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static <T> T query(PreparedStatement statement, ResultSetMapper<T> resultSetMapper) throws SQLException {
		ResultSet resultSet = null;
		try {
			resultSet = statement.executeQuery();
			return resultSetMapper.mapper(resultSet);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
	}

	public static boolean execute(Connection connection, Sql sql) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createPreparedStatement(connection, sql);
			return statement.execute();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static int update(Connection connection, Sql sql) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = createPreparedStatement(connection, sql);
			return statement.executeUpdate();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public static Object[] getRowValues(ResultSet resultSet, int size) throws SQLException {
		Object[] values = new Object[size];
		for (int i = 1; i <= size; i++) {
			values[i - 1] = resultSet.getObject(i);
		}
		return values;
	}

	public static String toLikeValue(String value) {
		if (StringUtils.isEmpty(value)) {
			return "%";
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			sb.append("%");
			sb.append(value.charAt(i));
		}
		sb.append("%");
		return sb.toString();
	}

	public static void executeSqlByFile(SqlOperations sqlOperations, String filePath, boolean lines)
			throws SQLException {
		Collection<Sql> sqls = getSqlByFile(filePath, lines);
		for (Sql sql : sqls) {
			sqlOperations.execute(sql);
		}
	}

	public static Collection<Sql> getSqlByFile(String path, boolean lines) {
		LinkedList<Sql> list = new LinkedList<Sql>();
		if (lines) {
			Collection<String> sqlList = ResourceUtils.getResourceOperations().getLines(path,
					Constants.DEFAULT_CHARSET_NAME);
			for (String sql : sqlList) {
				if (sql.startsWith(IGNORE_SQL_START_WITH)) {
					continue;
				}

				list.add(new SimpleSql(sql));
			}
		} else {
			String sql = ResourceUtils.getResourceOperations().getContent(path, Constants.DEFAULT_CHARSET_NAME);
			if (!StringUtils.isEmpty(sql)) {
				list.add(new SimpleSql(sql));
			}
		}
		return list;
	}
	
	public static boolean isDataBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type)
				|| String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| java.util.Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type)
				|| Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type)
				|| NClob.class.isAssignableFrom(type);
	}
}
