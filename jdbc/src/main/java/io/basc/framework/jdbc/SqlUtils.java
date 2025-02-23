package io.basc.framework.jdbc;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Pair;
import io.basc.framework.util.Range;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.LinkedMultiValueMap;
import io.basc.framework.util.collections.MultiValueMap;
import io.basc.framework.util.collections.Streams;

public final class SqlUtils {
	private static final String PATTERN = "(|_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)";
	/**
	 * 参数占位符
	 */
	private static final String PARAMETER_PLACEHOLDER = "?";
	private static final String SET = " set ";
	private static final String WHERE = " where ";
	private static final String INSERT_VALUES = " values ";
	private static final String UPDATE_PREFIX = "update ";
	private static final String INSERT_PREFIX = "insert into ";

	public static String toString(Sql sql) {
		if (sql == null) {
			return null;
		}

		if (sql instanceof SimpleSql) {
			return sql.toString();
		}

		if (sql.isStoredProcedure()) {
			return "Stored procedure " + toString(sql.getSql(), sql.getParams());
		}
		return toString(sql.getSql(), sql.getParams());
	}

	public static String toString(String sql, Object... args) {
		if (args == null || args.length == 0) {
			return sql;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(sql);
			sb.append("]");
			sb.append(" - ");
			sb.append(Arrays.toString(args));
			return sb.toString();
		}
	}

	public static int getParameterPlaceholderCount(String sql) {
		return StringUtils.count(sql, PARAMETER_PLACEHOLDER);
	}

	public static void setSqlParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args == null || args.length == 0) {
			return;
		}

		for (int i = 0; i < args.length; i++) {
			Object value = args[i];
			if (value != null) {
				if (value instanceof Enum) {
					value = ((Enum<?>) value).name();
				}
			}
			preparedStatement.setObject(i + 1, value);
		}
	}

	public static Object[] getRowValues(ResultSet resultSet, int size) throws SQLException {
		Object[] values = new Object[size];
		for (int i = 1; i <= size; i++) {
			values[i - 1] = resultSet.getObject(i);
		}
		return values;
	}

	public static String[] getColumnNames(ResultSetMetaData rsmd) throws SQLException {
		return getColumnNames(rsmd, rsmd.getColumnCount());
	}

	public static String[] getColumnNames(ResultSetMetaData rsmd, int size) throws SQLException {
		String[] names = new String[size];
		for (int i = 1; i <= size; i++) {
			names[i - 1] = lookupColumnName(rsmd, i);
		}
		return names;
	}

	public static PreparedStatement preparedStatement(Connection connection, Sql sql) throws SQLException {
		PreparedStatement preparedStatement;
		if (sql.isStoredProcedure()) {
			preparedStatement = connection.prepareCall(sql.getSql());
		} else {
			preparedStatement = connection.prepareStatement(sql.getSql());
		}
		try {
			SqlUtils.setSqlParams(preparedStatement, sql.getParams());
		} catch (SQLException e) {
			try {
				preparedStatement.close();
			} catch (SqlException e2) {
				e.addSuppressed(e2);
			}
			throw e;
		}
		return preparedStatement;
	}

	public static boolean isDataBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type) || java.util.Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type) || Timestamp.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type);
	}

	/**
	 * Determine the column name to use. The column name is determined based on a
	 * lookup using ResultSetMetaData.
	 * <p>
	 * This method implementation takes into account recent clarifications expressed
	 * in the JDBC 4.0 specification:
	 * <p>
	 * <i>columnLabel - the label for the column specified with the SQL AS clause.
	 * If the SQL AS clause was not specified, then the label is the name of the
	 * column</i>.
	 * 
	 * @param resultSetMetaData the current meta-data to use
	 * @param columnIndex       the index of the column for the look up
	 * @return the column name to use
	 * @throws SQLException in case of lookup failure
	 */
	public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if (StringUtils.isEmpty(name)) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}

	public static SqlException throwableSqlException(Throwable e, Supplier<String> desc) {
		if (e instanceof SqlException) {
			return (SqlException) e;
		}
		if (desc == null) {
			return new SqlException(e);
		}
		return new SqlException(desc.get(), e);
	}

	public static MultiValueMap<String, Object> getRowValueMap(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>();
		for (int i = 1; i <= cols; i++) {
			String name = SqlUtils.lookupColumnName(metaData, i);
			Object value = rs.getObject(i);
			values.add(name, value);
		}
		return values;
	}

	public static Stream<SqlSplitSegment> split(Sql sql, CharSequence... filters) {
		return split(sql, 0, filters);
	}

	public static Stream<SqlSplitSegment> split(Sql sql, int start, CharSequence... filters) {
		return split(sql, start, sql.getSql().length(), filters);
	}

	public static Stream<SqlSplitSegment> split(Sql sql, int start, int end, CharSequence... filters) {
		return split(sql, start, end, Arrays.asList(filters));
	}

	public static Stream<SqlSplitSegment> split(Sql sql, Collection<? extends CharSequence> filters) {
		return split(sql, 0, filters);
	}

	public static Stream<SqlSplitSegment> split(Sql sql, int start, Collection<? extends CharSequence> filters) {
		return split(sql, start, sql.getSql().length(), filters);
	}

	public static Stream<SqlSplitSegment> split(Sql sql, int start, int end,
			Collection<? extends CharSequence> filters) {
		return Streams.stream(new SqlSplitIterator(sql, filters, start, end));
	}

	public static Sql sub(Sql sql, int start) {
		Assert.requiredArgument(sql != null, "sql");
		String sourceSql = sql.getSql();
		return sub(sourceSql, sql.getParams(), sql.isStoredProcedure(), start, sourceSql.length());
	}

	public static Sql sub(Sql sql, int start, int end) {
		Assert.requiredArgument(sql != null, "sql");
		return sub(sql.getSql(), sql.getParams(), sql.isStoredProcedure(), start, end);
	}

	public static Sql sub(String sourceSql, Object[] sourceParams, boolean storedProcedure, int start, int end) {
		Assert.requiredArgument(sourceSql != null, "sourceSql");
		Assert.requiredArgument(sourceParams != null, "sourceParams");

		if (sourceParams.length != getParameterPlaceholderCount(sourceSql)) {
			// 参数占位符数量和参数数量不一致
			throw new IllegalSqlException(
					"The number of parameter placeholders is inconsistent with the number of parameters <"
							+ toString(sourceSql, sourceParams) + ">");
		}

		String targetSql = sourceSql.substring(start, end);
		Object[] targetParams;
		if (sourceParams.length == 0) {
			targetParams = new Object[0];
		} else {
			int len = StringUtils.count(sourceSql, start, end, PARAMETER_PLACEHOLDER);
			int startIndex = StringUtils.count(sourceSql, 0, start, PARAMETER_PLACEHOLDER);
			targetParams = new Object[len];
			for (int i = startIndex, index = 0; i < startIndex + len; i++, index++) {
				targetParams[index] = sourceParams[i];
			}
		}
		return new SimpleSql(storedProcedure, targetSql, targetParams);
	}

	public static SqlExpression resolveExpression(Sql sql, Collection<? extends CharSequence> filters) {
		String sourceSql = sql.getSql();
		for (CharSequence filter : filters) {
			int index = StringUtils.indexOf(sourceSql, filter);
			if (index == -1) {
				continue;
			}

			String name = sourceSql.substring(0, index);
			name = name.trim();
			name = StringUtils.trimLeadingCharacter(name, '`');
			name = StringUtils.trimTrailingCharacter(name, '`');
			if (!Pattern.matches(PATTERN, name)) {
				// 不合法的字段名
				continue;
			}

			return new SqlExpression(name, sub(sql, 0, index), filter, sub(sql, index + filter.length()));
		}
		return null;
	}

	public static List<SqlSplitSegment> resolveSegments(Sql sql, Collection<? extends CharSequence> separators,
			Predicate<Sql> accept) {
		LinkedList<SqlSplitSegment> list = new LinkedList<SqlSplitSegment>();
		Iterator<SqlSplitSegment> iterator = split(sql, separators).iterator();
		while (iterator.hasNext()) {
			SqlSplitSegment segment = iterator.next();
			if (accept.test(segment)) {
				// 合法的片段
				list.add(segment);
			} else {
				SqlSplitSegment last = list.removeLast();
				EasySql item = new EasySql();
				item.append(last);
				item.append(last.getSeparator());
				item.append(segment);
				list.addLast(new SqlSplitSegment(item, segment.getSeparator()));
			}
		}
		return list;
	}

	public static Map<String, SqlExpression> resolveExpressionMap(Sql sql,
			Collection<? extends CharSequence> separators, Collection<? extends CharSequence> filters) {
		Map<String, SqlExpression> map = new LinkedHashMap<String, SqlExpression>(8);
		resolveSegments(sql, separators, (s) -> (resolveExpression(s, filters) != null)).forEach((s) -> {
			SqlExpression expression = resolveExpression(s, filters);
			map.put(expression.getName(), expression);
		});
		return map;
	}

	public static Map<String, SqlExpression> resolveUpdateSetMap(Sql sql) {
		String update = sql.getSql();
		// 全部转小写
		update = update.toLowerCase();
		int setIndex = update.indexOf(SET);
		int whereIndex = update.indexOf(WHERE, setIndex == -1 ? 0 : setIndex);
		if (setIndex == -1) {
			// TODO 如果不存在set那么当作这只是update语句的一部分来处理
			setIndex = 0;
		} else {
			setIndex += SET.length();
		}
		Sql set;
		if (whereIndex == -1) {
			set = SqlUtils.sub(sql, setIndex);
		} else {
			set = SqlUtils.sub(sql, setIndex, whereIndex);
		}
		return resolveExpressionMap(set, Arrays.asList(","), Arrays.asList("="));
	}

	public static Sql resolveWhereSql(Sql sql, int start, int end) {
		String sourceSql = sql.getSql();
		sourceSql = sourceSql.toLowerCase();
		int index = StringUtils.indexOf(sourceSql, WHERE, start, end);
		if (index == -1) {
			return null;
		}
		return sub(sql, index + WHERE.length(), end);
	}

	public static Sql resolveUpdateWhereSql(Sql sql) {
		String sourceSql = sql.getSql();
		// 全部转小写
		sourceSql = sourceSql.toLowerCase();
		int setIndex = sourceSql.indexOf(SET);
		if (setIndex == -1) {
			// 不应该出现这种情况，一个update语句不能没有set
			throw new SqlException("An update statement cannot have no set: <" + toString(sql) + ">");
		}
		return resolveWhereSql(sql, setIndex + SET.length(), sourceSql.length());
	}

	public static List<Sql> resolveInsertValues(Sql sql) {
		return split(resolveInsertValuesSql(sql), ",").collect(Collectors.toList());
	}

	public static Sql resolveInsertValuesSql(Sql sql) {
		String sourceSql = sql.getSql();
		sourceSql = sourceSql.toLowerCase();
		int valuesIndex = sourceSql.indexOf(INSERT_VALUES);
		if (valuesIndex == -1) {
			throw new SqlException("The inser statement must have values keyword: <" + toString(sql) + ">");
		}

		Range<Integer> range = StringUtils.indexOf(sourceSql, "(", ")", valuesIndex + INSERT_VALUES.length(),
				sourceSql.length());
		if (range == null) {
			throw new SqlException("The inser statement must have values: <" + toString(sql) + ">");
		}
		return sub(sql, range.getLowerBound().get() + 1, range.getUpperBound().get());
	}

	public static List<Sql> resolveInsertColumns(Sql sql) {
		Sql columns = resolveInsertColumnsSql(sql);
		if (columns == null) {
			// 没有显示声明columns,说明是插入全表字段的语句
			return Collections.emptyList();
		}

		return split(columns, ",").collect(Collectors.toList());
	}

	@Nullable
	public static Sql resolveInsertColumnsSql(Sql sql) {
		String sourceSql = sql.getSql();
		sourceSql = sourceSql.toLowerCase();
		int valuesIndex = sourceSql.indexOf(INSERT_VALUES);
		if (valuesIndex == -1) {
			throw new SqlException("The inser statement must have values keyword: <" + toString(sql) + ">");
		}

		Pair<Integer, Integer> pairIndex = StringUtils.indexOf(sourceSql, "(", ")", 0, valuesIndex);
		if (pairIndex == null) {
			// 没有显示声明columns,说明是插入全表字段的语句
			return null;
		}

		return sub(sql, pairIndex.getKey() + 1, pairIndex.getValue());
	}

	public static Sql resolveUpdateTables(Sql sql) {
		String sourceSql = sql.getSql();
		sourceSql = sourceSql.toLowerCase();
		if (!sourceSql.startsWith(UPDATE_PREFIX)) {
			throw new SqlException("This is not an update statement: <" + toString(sql) + ">");
		}

		int endIndex = sourceSql.indexOf(SET, UPDATE_PREFIX.length());
		if (endIndex == -1) {
			throw new SqlException("An update statement cannot have no set: <" + toString(sql) + ">");
		}

		return sub(sql, UPDATE_PREFIX.length(), endIndex);
	}

	public static Sql resolveInsertTables(Sql sql) {
		String sourceSql = sql.getSql();
		sourceSql = sourceSql.toLowerCase();
		if (!sourceSql.startsWith(INSERT_PREFIX)) {
			throw new SqlException("This is not an insert statement: <" + toString(sql) + ">");
		}

		// TODO 也许不严格吧
		int endIndex = sourceSql.indexOf("(", INSERT_PREFIX.length());
		if (endIndex == -1) {
			throw new IllegalSqlException(toString(sql));
		}

		return sub(sql, INSERT_PREFIX.length(), endIndex);
	}

	public static String display(Sql sql) {
		StringBuilder sb = new StringBuilder();
		Object[] args = sql.getParams();
		String sourceSql = sql.getSql();
		int lastFind = 0;
		for (int i = 0; i < args.length; i++) {
			int index = sourceSql.indexOf(PARAMETER_PLACEHOLDER, lastFind);
			if (index == -1) {
				break;
			}

			sb.append(sourceSql.substring(lastFind, index));
			Object v = args[i];
			if (v == null) {
				sb.append("null");
			} else {
				if (ClassUtils.isPrimitiveOrWrapper(v.getClass()) && !ClassUtils.isChar(v.getClass())) {
					sb.append(v);
				} else {
					sb.append("'").append(StringUtils.transferredMeaning(String.valueOf(v), '\'')).append("'");
				}
			}
			lastFind = index + 1;
		}

		if (lastFind == 0) {
			sb.append(sourceSql);
		} else {
			sb.append(sourceSql.substring(lastFind));
		}
		return sb.toString();
	}

	public static void deregisterDriver() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		if (drivers != null) {
			while (drivers.hasMoreElements()) {
				try {
					DriverManager.deregisterDriver(drivers.nextElement());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
