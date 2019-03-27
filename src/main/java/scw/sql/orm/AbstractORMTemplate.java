package scw.sql.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.beans.BeanFieldListen;
import scw.common.Pagination;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.sql.ResultSetMapper;
import scw.sql.Sql;
import scw.sql.SqlException;
import scw.sql.SqlTemplate;
import scw.sql.orm.annoation.Table;
import scw.sql.orm.enums.OperationType;
import scw.sql.orm.mysql.MysqlSelect;
import scw.sql.orm.result.DefaultResultSet;
import scw.sql.orm.result.ResultSet;
import scw.transaction.cache.QueryCacheUtils;

public abstract class AbstractORMTemplate extends SqlTemplate implements SqlSelect, ORMOperations, SelectMaxId {
	
	public abstract SqlFormat getSqlFormat();

	public <T> T getById(Class<T> type, Object... params) {
		return getById(null, type, params);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getByIdList(null, type, params);
	}

	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
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
		Sql sql = getSqlFormat().toSelectByIdSql(tableInfo, tName, params);
		ResultSet resultSet = select(sql);
		return resultSet.getFirst().get(type, tName);
	}

	public <T> List<T> getByIdList(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
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

	public boolean save(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		OperationBean operationBean = new OperationBean(OperationType.SAVE, bean, tableName);
		Sql sql = operationBean.format(getSqlFormat());
		if (sql == null) {
			return false;
		}
		return execute(sql);
	}

	public boolean update(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		if (bean instanceof BeanFieldListen) {
			if (((BeanFieldListen) bean).get_field_change_map() == null) {
				return false;
			}
		}

		OperationBean operationBean = new OperationBean(OperationType.UPDATE, bean, tableName);
		Sql sql = operationBean.format(getSqlFormat());
		if (sql == null) {
			return false;
		}
		return execute(sql);
	}

	public boolean delete(Object bean) {
		return delete(bean, null);
	}

	public boolean delete(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		OperationBean operationBean = new OperationBean(OperationType.DELETE, bean, tableName);
		Sql sql = operationBean.format(getSqlFormat());
		if (sql == null) {
			return false;
		}

		return execute(sql);
	}

	public boolean deleteById(Class<?> type, Object... params) {
		return deleteById(null, type, params);
	}

	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		if (type == null) {
			return false;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length != params.length) {
			throw new SqlException("主键数量和参数不一致:" + type.getName());
		}

		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getName() : tableName;
		Sql sql = getSqlFormat().toDeleteSql(tableInfo, tName, params);
		return execute(sql);
	}

	public boolean saveOrUpdate(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		OperationBean operationBean = new OperationBean(OperationType.SAVE_OR_UPDATE, bean, tableName);
		Sql sql = operationBean.format(getSqlFormat());
		if (sql == null) {
			return false;
		}

		return execute(sql);
	}

	public boolean save(Object bean) {
		return save(bean, null);
	}

	public boolean update(Object bean) {
		return update(bean, null);
	}

	public boolean saveOrUpdate(Object bean) {
		return saveOrUpdate(bean, null);
	}

	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (inIds == null || inIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
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
			return Collections.EMPTY_MAP;
		}

		Map<K, V> map = new HashMap<K, V>();
		for (V v : list) {
			K k;
			try {
				k = (K) columnInfo.getFieldInfo().forceGet(v);
				if (map.containsKey(k)) {
					throw new AlreadyExistsException(k + "");
				}
				map.put(k, v);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIdList, Object... params) {
		return getInIdList(type, null, inIdList, params);
	}

	public ResultSet select(Sql sql) {
		return QueryCacheUtils.query(this, sql, new ResultSetMapper<ResultSet>() {

			public ResultSet mapper(java.sql.ResultSet resultSet) throws SQLException {
				return new DefaultResultSet(resultSet);
			}
		});
	}

	public <T> List<T> select(Class<T> type, Sql sql) {
		return select(sql).getList(type);
	}

	public <T> T selectOne(Class<T> type, Sql sql) {
		return select(sql).getFirst().get(type);
	}

	@SuppressWarnings("unchecked")
	public <T> T selectOne(Class<T> type, Sql sql, T defaultValue) {
		if (type.isPrimitive()) {
			// 如果是基本数据类型
			Object v = selectOne(ClassUtils.resolvePrimitiveIfNecessary(type), sql);
			return (T) (v == null ? defaultValue : v);
		} else {
			T v = selectOne(type, sql);
			return v == null ? defaultValue : v;
		}
	}
	
	public void createDataBase(Connection connection, String databaseName, String charsetName, String collate){
		
	}
	
	public void createTable(Class<?> tableClass) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		createTable(tableClass, tableInfo.getName());
	}

	public void createTable(Class<?> tableClass, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		Sql sql = getSqlFormat().toCreateTableSql(tableInfo, tableName);
		execute(sql);
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

	@SuppressWarnings("unchecked")
	public <T> Pagination<List<T>> select(Class<T> type, long page, int limit, Sql sql) {
		PaginationSql paginationSql = getSqlFormat().toPaginationSql(sql, page, limit);
		Long count = selectOne(Long.class, paginationSql.getCountSql());
		if (count == null) {
			count = 0L;
		}

		if (count == 0) {
			return new Pagination<List<T>>(0, limit, Collections.EMPTY_LIST);
		}

		return new Pagination<List<T>>(count, limit, select(type, paginationSql.getResultSql()));
	}

	public <T> Pagination<List<T>> select(Class<T> type, int page, int limit, Sql sql) {
		return select(type, (long) page, limit, sql);
	}

	public Pagination<ResultSet> select(long page, int limit, Sql sql) {
		PaginationSql paginationSql = getSqlFormat().toPaginationSql(sql, page, limit);
		Long count = selectOne(Long.class, paginationSql.getCountSql());
		if (count == null) {
			count = 0L;
		}

		if (count == 0) {
			return new Pagination<ResultSet>(0, limit, ResultSet.EMPTY_RESULTSET);
		}

		return new Pagination<ResultSet>(count, limit, select(paginationSql.getResultSql()));
	}

	public Pagination<ResultSet> select(int page, int limit, Sql sql) {
		return select((long) page, limit, sql);
	}

	public Select createSelect() {
		return new MysqlSelect(this);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String columnName) {
		Select select = createSelect();
		select.desc(tableClass, columnName);
		return select.getResultSet().getFirst().get(type, tableName);
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
}
