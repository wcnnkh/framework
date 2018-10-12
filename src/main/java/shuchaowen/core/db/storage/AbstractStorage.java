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
import shuchaowen.core.db.sql.format.mysql.MysqlFormat;

public abstract class AbstractStorage implements Storage{
	public static final SQLFormat DEFAULT_SQL_FORMAT = new MysqlFormat();
	
	private final AbstractDB db;
	private final SQLFormat sqlFormat;
	
	public AbstractStorage(AbstractDB db){
		this(db, DEFAULT_SQL_FORMAT);
	}
	
	public AbstractStorage(AbstractDB db, SQLFormat sqlFormat){
		this.db = db;
		this.sqlFormat = sqlFormat;
	}
	
	public AbstractDB getDb() {
		return db;
	}
	
	public SQLFormat getSqlFormat() {
		return sqlFormat == null? DEFAULT_SQL_FORMAT:sqlFormat;
	}
	
	protected <T> T getByIdForDB(Class<T> type, String tableName, Object... params) {
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
		SQL sql = getSqlFormat().toSelectByIdSql(tableInfo, tName, params);
		ResultSet resultSet = getDb().select(sql);
		resultSet.registerClassTable(type, tName);
		return resultSet.getFirst(type);
	}

	protected <T> List<T> getByIdListForDB(String tableName, Class<T> type,
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
		ResultSet resultSet = getDb().select(getSqlFormat().toSelectByIdSql(tableInfo, tName, params));
		resultSet.registerClassTable(type, tName);
		return resultSet.getList(type);
	}

	protected List<SQL> getSaveSqlList(Collection<Object> beans) {
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}

			sqls.add(getSqlFormat().toInsertSql(obj));
		}
		return sqls;
	}

	protected void saveToDB(Collection<Object> beans) {
		getDb().execute(getSaveSqlList(beans));
	}

	protected List<SQL> getUpdateSqlList(Collection<Object> beans) {
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(getSqlFormat().toUpdateSql(obj));
		}
		return sqls;
	}

	protected void updateToDB(Collection<Object> beans) {
		getDb().execute(getUpdateSqlList(beans));
	}

	protected List<SQL> getDeleteSqlList(Collection<Object> beans) {
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(getSqlFormat().toDeleteSql(obj));
		}
		return sqls;
	}

	protected void deleteToDB(Collection<Object> beans) {
		getDb().execute(getDeleteSqlList(beans));
	}

	protected List<SQL> getSaveOrUpdateSqlList(Collection<Object> beans) {
		if (beans == null) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(getSqlFormat().toSaveOrUpdateSql(obj));
		}
		return sqls;
	}

	protected void saveOrUpdateToDB(Collection<Object> beans) {
		getDb().execute(getSaveOrUpdateSqlList(beans));
	}

	protected <T> Map<PrimaryKeyParameter, T> getByIdForDB(Class<T> type,
			String tableName, Collection<PrimaryKeyParameter> primaryKeyParameters) {
		TableInfo tableInfo = DB.getTableInfo(type);
		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		SQL sql = getSqlFormat().toSelectINId(tableInfo, tName, primaryKeyParameters);
		ResultSet resultSet = getDb().select(sql);
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
	
	public <T> T getById(Class<T> type, Object... params) {
		return getByIdForDB(type, null, params);
	}

	public <T> Map<PrimaryKeyParameter, T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		return getByIdForDB(type, null, primaryKeyParameters);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getByIdListForDB(null, type, params);
	}

	public void save(Collection<Object> beans) {
		execute(new ExecuteInfo(EOperationType.SAVE, beans));
	}

	public void update(Collection<Object> beans) {
		execute(new ExecuteInfo(EOperationType.UPDATE, beans));
	}

	public void delete(Collection<Object> beans) {
		execute(new ExecuteInfo(EOperationType.DELETE, beans));
	}

	public void saveOrUpdate(Collection<Object> beans) {
		execute(new ExecuteInfo(EOperationType.SAVE_OR_UPDATE, beans));
	}
	
	protected Collection<SQL> getSqlList(ExecuteInfo executeInfo){
		switch (executeInfo.getOperationType()) {
		case SAVE:
			return getSaveSqlList(executeInfo.getBeanList());
		case DELETE:
			return getDeleteSqlList(executeInfo.getBeanList());
		case UPDATE:
			return getUpdateSqlList(executeInfo.getBeanList());
		case SAVE_OR_UPDATE:
			return getSaveOrUpdateSqlList(executeInfo.getBeanList());
		default:
			break;
		}
		return null;
	}
	
	public abstract void execute(ExecuteInfo executeInfo);
}
