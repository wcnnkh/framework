package scw.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.common.Iterator;
import scw.common.Logger;
import scw.common.Pagination;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.database.ColumnInfo;
import scw.database.ConnectionSource;
import scw.database.DataBaseUtils;
import scw.database.SQL;
import scw.database.TableInfo;
import scw.database.TransactionContext;
import scw.database.annoation.Table;
import scw.database.result.Result;
import scw.database.result.ResultSet;
import scw.db.sql.MysqlFormat;
import scw.db.sql.MysqlSelect;
import scw.db.sql.PaginationSql;
import scw.db.sql.SQLFormat;
import scw.db.sql.Select;

public abstract class AbstractDB implements ConnectionSource, AutoCloseable {
	{
		Logger.info("Init DB for className:" + this.getClass().getName());
	}

	private final SQLFormat sqlFormat;

	public AbstractDB(SQLFormat sqlFormat) {
		this.sqlFormat = sqlFormat == null ? new MysqlFormat() : sqlFormat;
	}

	public final SQLFormat getSqlFormat() {
		return sqlFormat;
	}

	public void iterator(Class<?> tableClass, Iterator<Result> iterator) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		iterator(getSqlFormat().toSelectByIdSql(tableInfo, tableInfo.getName(), null), iterator);
	}

	public void iterator(SQL sql, final Iterator<Result> iterator) {
		DataBaseUtils.iterator(this, sql, new Iterator<java.sql.ResultSet>() {

			public void iterator(java.sql.ResultSet data) {
				try {
					iterator.iterator(new Result(data));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ResultSet select(SQL sql) {
		return select(sql, true);
	}

	public ResultSet select(SQL sql, boolean isTransactionCache) {
		return TransactionContext.getInstance().select(this, sql, isTransactionCache);
	}

	public Pagination<ResultSet> select(long page, int limit, SQL sql) {
		PaginationSql paginationSql = sqlFormat.toPaginationSql(sql, page, limit);
		Long count = selectOne(Long.class, paginationSql.getCountSql());
		if (count == null) {
			count = 0L;
		}

		if (count == 0) {
			return new Pagination<ResultSet>(0, limit, null);
		}

		return new Pagination<ResultSet>(count, limit, select(paginationSql.getResultSql()));
	}

	public <T> Pagination<List<T>> select(Class<T> type, long page, int limit, SQL sql) {
		PaginationSql paginationSql = sqlFormat.toPaginationSql(sql, page, limit);
		Long count = selectOne(Long.class, paginationSql.getCountSql());
		if (count == null) {
			count = 0L;
		}

		if (count == 0) {
			return new Pagination<List<T>>(0, limit, null);
		}

		return new Pagination<List<T>>(count, limit, select(type, paginationSql.getResultSql()));
	}

	public <T> List<T> select(Class<T> type, SQL sql) {
		return select(sql).getList(type);
	}

	public <T> T selectOne(Class<T> type, SQL sql) {
		return select(sql).getObject(type, 0);
	}

	public Select createSelect() {
		return new MysqlSelect(this);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String columnName) {
		Select select = createSelect();
		select.desc(tableClass, columnName);
		return select.getResultSet().getObject(type, 0, tableName);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String columnName) {
		return getMaxValue(type, tableClass, null, columnName);
	}

	public int getMaxIntValue(Class<?> tableClass, String fieldName) {
		Integer maxId = getMaxValue(Integer.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0;
		}
		return maxId;
	}

	public long getMaxLongValue(Class<?> tableClass, String fieldName) {
		Long maxId = getMaxValue(Long.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0L;
		}
		return maxId;
	}

	public void createTable(Class<?> tableClass) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		createTable(tableClass, tableInfo.getName());
	}

	public void createTable(Class<?> tableClass, String tableName) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		SQL sql = getSqlFormat().toCreateTableSql(tableInfo, tableName);
		Logger.info(sql.getSql());
		DataBaseUtils.execute(this, Arrays.asList(sql));
	}

	public void createTable(String packageName) {
		Collection<Class<?>> list = ClassUtils.getClasses(packageName);
		for (Class<?> tableClass : list) {
			Table table = tableClass.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (table.create()) {
				createTable(tableClass);
			}
		}
	}

	public <T> T getById(Class<T> type, Object... params) {
		return getById(null, type, params);
	}

	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
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
		ResultSet resultSet = select(sql);
		return resultSet.getObject(type, 0, tName);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getByIdList(null, type, params);
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIdList, Object... params) {
		return getInIdList(type, null, inIdList, params);
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (inIds == null || inIds.isEmpty()) {
			return null;
		}

		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[params.length];
		if (params.length > tableInfo.getPrimaryKeyColumns().length - 1) {
			throw new NullPointerException("params length  greater than primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectInIdSql(tableInfo, tName, params, inIds));
		List<V> list = resultSet.getList(type, tName);
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<K, V> map = new HashMap<K, V>();
		for (V v : list) {
			@SuppressWarnings("unchecked")
			K k = (K) columnInfo.dbValueToFieldValue(v);
			if (map.containsKey(k)) {
				throw new AlreadyExistsException(k + "");
			}
			map.put(k, v);
		}
		return map;
	}

	public <T> List<T> getByIdList(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new NullPointerException("params length  greater than primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectByIdSql(tableInfo, tName, params));
		return resultSet.getList(type, tName);
	}

	public void execute(Collection<OperationBean> operationBeans) {
		Collection<SQL> sqls = DBUtils.getSqlList(getSqlFormat(), operationBeans);
		if (sqls == null || sqls.isEmpty()) {
			return;
		}

		TransactionContext.getInstance().execute(this, sqls);
	}

	public void save(Object... beans) {
		save(null, Arrays.asList(beans));
	}

	/** 保存 **/
	public void save(String tableName, Object... beans) {
		save(tableName, Arrays.asList(beans));
	}

	public void save(String tableName, Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.SAVE, bean, tableName));
		}

		execute(operationBeans);
	}

	public void delete(Object... beans) {
		delete(null, Arrays.asList(beans));
	}

	/** 删除 **/
	public void delete(String tableName, Object... beans) {
		delete(tableName, Arrays.asList(beans));
	}

	public void delete(Class<?> tableClass, Object... params) {
		delete(tableClass, null, params);
	}

	public void delete(Class<?> tableClass, String tableName, Object... params) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		String tName = StringUtils.isNull(tableName) ? tableInfo.getName() : tableName;
		SQL sql = getSqlFormat().toDeleteSql(tableInfo, tName, params);
		TransactionContext.getInstance().execute(this, sql);
	}

	public void delete(String tableName, Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.DELETE, bean, tableName));
		}

		execute(operationBeans);
	}

	public void update(Object... beans) {
		update(null, Arrays.asList(beans));
	}

	/** 更新 **/
	public void update(String tableName, Object... beans) {
		update(tableName, Arrays.asList(beans));
	}

	public void update(Class<?> tableClass, Map<String, Object> valueMap, Object... params) {
		update(tableClass, null, valueMap, params);
	}

	public void update(Class<?> tableClass, String tableName, Map<String, Object> valueMap, Object... params) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		String tName = StringUtils.isNull(tableName) ? tableInfo.getName() : tableName;
		SQL sql = getSqlFormat().toUpdateSql(tableInfo, tName, valueMap, params);
		TransactionContext.getInstance().execute(this, sql);
	}

	public void update(String tableName, Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.UPDATE, bean, tableName));
		}

		execute(operationBeans);
	}

	public void saveOrUpdate(Object... beans) {
		saveOrUpdate(null, Arrays.asList(beans));
	}

	/** 保存或更新 **/
	public void saveOrUpdate(String tableName, Object... beans) {
		saveOrUpdate(tableName, Arrays.asList(beans));
	}

	public void saveOrUpdate(String tableName, Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.SAVE_OR_UPDATE, bean, tableName));
		}

		execute(operationBeans);
	}
}
