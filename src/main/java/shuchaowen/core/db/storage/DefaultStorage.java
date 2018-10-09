package shuchaowen.core.db.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.format.SQLFormat;

public class DefaultStorage implements Storage {
	public <T> T getById(AbstractDB db, SQLFormat sqlFormat, Class<T> type, Object... params) {
		return getById(db, sqlFormat, type, null, params);
	}

	protected <T> T getById(AbstractDB db, SQLFormat sqlFormat, Class<T> type, String tableName, Object... params) {
		if (db == null) {
			throw new NullPointerException("connectionOrigin is null");
		}

		if (sqlFormat == null) {
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
			throw new NullPointerException("params length not equals primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		SQL sql = sqlFormat.toSelectByIdSql(tableInfo, tName, params);
		ResultSet resultSet = db.select(sql);
		resultSet.registerClassTable(type, tName);
		return resultSet.getFirst(type);
	}

	public <T> List<T> getByIdList(AbstractDB db, SQLFormat sqlFormat, Class<T> type, Object... params) {
		return getByIdList(db, sqlFormat, null, type, params);
	}

	protected <T> List<T> getByIdList(AbstractDB db, SQLFormat sqlFormat, String tableName, Class<T> type,
			Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DB.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new NullPointerException("params length  greater than primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		ResultSet resultSet = db.select(sqlFormat.toSelectByIdSql(tableInfo, tName, params));
		resultSet.registerClassTable(type, tName);
		return resultSet.getList(type);
	}

	protected List<SQL> getSaveSqlList(Collection<Object> beans, SQLFormat sqlFormat) {
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

	public void save(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat) {
		execute(db, getSaveSqlList(beans, sqlFormat));
	}

	protected List<SQL> getUpdateSqlList(Collection<Object> beans, SQLFormat sqlFormat) {
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

	public void update(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat) {
		execute(db, getUpdateSqlList(beans, sqlFormat));
	}

	protected List<SQL> getDeleteSqlList(Collection<Object> beans, SQLFormat sqlFormat) {
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

	public void delete(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat) {
		execute(db, getDeleteSqlList(beans, sqlFormat));
	}

	protected List<SQL> getSaveOrUpdateSqlList(Collection<Object> beans, SQLFormat sqlFormat) {
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

	public void saveOrUpdate(Collection<Object> beans, AbstractDB db, SQLFormat sqlFormat) {
		execute(db, getSaveOrUpdateSqlList(beans, sqlFormat));
	}

	public void execute(AbstractDB db, Collection<SQL> sqls) {
		db.execute(sqls);
	}

	public <T> Map<PrimaryKeyParameter, T> getById(AbstractDB db, SQLFormat sqlFormat, Class<T> type,
			Collection<PrimaryKeyParameter> parameters) {
		return getById(db, sqlFormat, type, null, parameters);
	}

	protected <T> Map<PrimaryKeyParameter, T> getById(AbstractDB db, SQLFormat sqlFormat, Class<T> type,
			String tableName, Collection<PrimaryKeyParameter> parameters) {
		TableInfo tableInfo = DB.getTableInfo(type);
		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		SQL sql = sqlFormat.toSelectINId(tableInfo, tName, parameters);
		ResultSet resultSet = db.select(sql);
		resultSet.registerClassTable(type, tName);
		List<T> list = resultSet.getList(type);
		Map<PrimaryKeyParameter, T> map = new HashMap<PrimaryKeyParameter, T>();
		for (T t : list) {
			try {
				map.put(tableInfo.getPrimaryKeyParameter(t), t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
}
