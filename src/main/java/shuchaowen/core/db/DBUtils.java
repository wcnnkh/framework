package shuchaowen.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.transaction.SQLTransaction;
import shuchaowen.core.util.XUtils;

public final class DBUtils {
	private DBUtils(){};
	
	public static void setParams(PreparedStatement preparedStatement, Object[] args) throws SQLException {
		if (args != null && args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				preparedStatement.setObject(i + 1, args[i]);
			}
		}
	}

	public static void iterator(ConnectionPool connectionPool, SQL sql, TableMapping tableMapping, ResultIterator iterator) {
		try {
			iterator(connectionPool.getConnection(), sql, tableMapping, iterator);
		} catch (SQLException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
	
	public static void iterator(Connection connection, SQL sql, TableMapping tableMapping, ResultIterator iterator) {
		if(sql == null || connection == null || iterator == null){
			return ;
		}
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			rs = stmt.executeQuery();
			while (rs.next()) {
				iterator.next(new Result(tableMapping, rs));
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
		sb.append(sql.getParams() == null ? "[]" : Arrays.toString(sql.getParams()));
		return sb.toString();
	}

	public static void execute(Connection connection, Collection<SQL> sqls) {
		if (sqls == null || connection == null) {
			throw new NullPointerException();
		}
		
		Iterator<SQL> iterator = sqls.iterator();
		if (sqls.size() == 1) {
			SQL sql = iterator.next();
			PreparedStatement stmt = null;
			try {
				stmt = connection.prepareStatement(sql.getSql());
				setParams(stmt, sql.getParams());
				stmt.execute();
			} catch (Exception e) {
				throw new ShuChaoWenRuntimeException(DBUtils.getSQLId(sql), e);
			} finally {
				XUtils.close(stmt, connection);
			}
		} else {
			SQLTransaction sqlTransaction = new SQLTransaction(connection);
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

	public static void execute(ConnectionPool connectionPool, Collection<SQL> sqls) {
		try {
			execute(connectionPool.getConnection(), sqls);
		} catch (SQLException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public static shuchaowen.core.db.result.ResultSet select(Connection connection, SQL sql) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(sql.getSql());
			setParams(stmt, sql.getParams());
			rs = stmt.executeQuery();
			return new shuchaowen.core.db.result.ResultSet(rs);
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(sql.getSql(), e);
		} finally {
			XUtils.close(rs, stmt, connection);
		}
	}

	public static shuchaowen.core.db.result.ResultSet select(ConnectionPool connectionPool, SQL sql) {
		try {
			return select(connectionPool.getConnection(), sql);
		} catch (SQLException e) {
			throw new ShuChaoWenRuntimeException(e);
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
	
	public static Collection<SQL> getSqlList(SQLFormat sqlFormat, Collection<OperationBean> operationBeans){
		if(operationBeans == null || operationBeans.isEmpty()){
			return null;
		}
		
		List<SQL> list = new ArrayList<SQL>();
		for(OperationBean operationBean : operationBeans){
			if(operationBean == null){
				continue;
			}
			
			list.add(operationBean.getSql(sqlFormat));
		}
		return list;
	}
	
	public static List<SQL> getSaveSqlList(SQLFormat sqlFormat, Collection<?> beans) {
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
	
	public static List<SQL> getUpdateSqlList(SQLFormat sqlFormat, Collection<?> beans) {
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
	
	public static List<SQL> getDeleteSqlList(SQLFormat sqlFormat, Collection<?> beans) {
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
	
	public static List<SQL> getSaveOrUpdateSqlList(SQLFormat sqlFormat, Collection<?> beans) {
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
