package shuchaowen.core.db.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.ConnectionOrigin;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;

public class DefaultStorage implements Storage{
	public <T> T getById(ConnectionOrigin connectionOrigin,
			SQLFormat sqlFormat, Class<T> type, Object... params) {
		return getById(connectionOrigin, sqlFormat, type, null, params);
	}
	
	protected <T> T getById(ConnectionOrigin connectionOrigin,
			SQLFormat sqlFormat, Class<T> type, String tableName, Object... params) {
		if(connectionOrigin == null){
			throw new NullPointerException("connectionOrigin is null");
		}
		
		if(sqlFormat == null){
			throw new NullPointerException("sqlFormat is null");
		}
		
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (tableInfo.getPrimaryKeyColumns().length != params.length) {
			throw new NullPointerException(
					"params length not equals primary key lenght");
		}
		
		String tName = (tableName == null || tableName.length() == 0)? tableInfo.getName():tableName;
			SQL sql = sqlFormat.toSelectByIdSql(tableInfo, tName, params);
			ResultSet resultSet = TransactionContext.getInstance().select(connectionOrigin, sql);;
			resultSet.registerClassTable(type, tName);
			return resultSet.getFirst(type);
	}
	
	public <T> List<T> getByIdList(ConnectionOrigin connectionOrigin,
			SQLFormat sqlFormat, Class<T> type, Object... params) {
		return getByIdList(connectionOrigin, sqlFormat, null, type, params);
	}

	protected <T> List<T> getByIdList(ConnectionOrigin connectionOrigin,
			SQLFormat sqlFormat, String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new NullPointerException(
					"params length  greater than primary key lenght");
		}
		
		String tName = (tableName == null || tableName.length() == 0)? tableInfo.getName():tableName;
		ResultSet resultSet = TransactionContext.getInstance().select(connectionOrigin, sqlFormat.toSelectByIdSql(tableInfo, tName, params));
		resultSet.registerClassTable(type, tName);
		return resultSet.getList(type);
	}
	
	protected List<SQL> getSaveSqlList(Collection<Object> beans, SQLFormat sqlFormat){
		if (beans == null) {
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
	
	public void save(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		TransactionContext.getInstance().execute(connectionOrigin, getSaveSqlList(beans, sqlFormat));
	}
	
	protected List<SQL> getUpdateSqlList(Collection<Object> beans, SQLFormat sqlFormat){
		if (beans == null) {
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

	public void update(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		TransactionContext.getInstance().execute(connectionOrigin, getUpdateSqlList(beans, sqlFormat));
	}

	
	protected List<SQL> getDeleteSqlList(Collection<Object> beans, SQLFormat sqlFormat){
		if (beans == null) {
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
	
	public void delete(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		TransactionContext.getInstance().execute(connectionOrigin, getDeleteSqlList(beans, sqlFormat));
	}
	
	protected List<SQL> getSaveOrUpdateSqlList(Collection<Object> beans, SQLFormat sqlFormat){
		if (beans == null) {
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

	public void saveOrUpdate(Collection<Object> beans,
			ConnectionOrigin connectionOrigin, SQLFormat sqlFormat) {
		TransactionContext.getInstance().execute(connectionOrigin, getSaveOrUpdateSqlList(beans, sqlFormat));
	}

	public void incr(Object obj, String field, double limit, Double maxValue, ConnectionOrigin connectionOrigin,
			SQLFormat sqlFormat) {
		SQL sql = sqlFormat.toIncrSql(obj, field, limit, maxValue);
		TransactionContext.getInstance().execute(connectionOrigin, Arrays.asList(sql));
	}

	public void decr(Object obj, String field, double limit, Double minValue, ConnectionOrigin connectionOrigin,
			SQLFormat sqlFormat) {
		SQL sql = sqlFormat.toDecrSql(obj, field, limit, minValue);
		TransactionContext.getInstance().execute(connectionOrigin, Arrays.asList(sql));
	}

}
