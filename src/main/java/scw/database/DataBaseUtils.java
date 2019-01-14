package scw.database;

import java.lang.reflect.Method;
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
import scw.beans.annotaion.Service;
import scw.common.Logger;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;
import scw.common.utils.XUtils;
import scw.database.annoation.SelectCache;
import scw.database.annoation.Table;
import scw.database.annoation.Transaction;
import scw.servlet.action.annotation.Controller;

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

	public static void setParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
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

	public static scw.database.result.ResultSet select(ConnectionSource connectionSource, SQL sql) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			rs = stmt.executeQuery();
			return new scw.database.result.ResultSet(rs);
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
				stmt = connection.prepareStatement(sql.getSql());
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
		Logger.debug(DataBaseUtils.class.getName(), "register proxy package:" + pageName);
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

	public static boolean isTransaction(Method method) {
		boolean isTransaction = false;
		Controller controller = method.getDeclaringClass().getAnnotation(Controller.class);
		if (controller != null) {
			isTransaction = true;
		}

		Service service = method.getDeclaringClass().getAnnotation(Service.class);
		if (service != null) {
			isTransaction = true;
		}

		Transaction transaction = method.getDeclaringClass().getAnnotation(Transaction.class);
		if (transaction != null) {
			isTransaction = transaction.value();
		}

		Transaction transaction2 = method.getAnnotation(Transaction.class);
		if (transaction2 != null) {
			isTransaction = transaction2.value();
		}

		return isTransaction;
	}

	public static boolean isSelectCache(Method method) {
		boolean isSelectCache = true;
		SelectCache selectCache = method.getDeclaringClass().getAnnotation(SelectCache.class);
		if (selectCache != null) {
			isSelectCache = selectCache.value();
		}

		SelectCache selectCache2 = method.getAnnotation(SelectCache.class);
		if (selectCache2 != null) {
			isSelectCache = selectCache2.value();
		}
		return isSelectCache;
	}
}
