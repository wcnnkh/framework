package run.soeasy.framework.jdbc;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
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
import java.util.Enumeration;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.LinkedMultiValueMap;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.lang.ClassUtils;

public final class JdbcUtils {
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

	public static MultiValueMap<String, Object> getRowValueMap(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int cols = metaData.getColumnCount();
		MultiValueMap<String, Object> values = new LinkedMultiValueMap<String, Object>(cols, 1);
		for (int i = 1; i <= cols; i++) {
			String name = JdbcUtils.lookupColumnName(metaData, i);
			Object value = rs.getObject(i);
			values.add(name, value);
		}
		return values;
	}

	public static void setParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
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
