package scw.sql.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.common.Iterator;
import scw.common.Pagination;
import scw.common.exception.AlreadyExistsException;
import scw.common.exception.NotFoundException;
import scw.common.exception.ParameterException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.sql.ResultSetMapper;
import scw.sql.RowCallback;
import scw.sql.Sql;
import scw.sql.SqlTemplate;
import scw.sql.orm.annoation.AutoCreate;
import scw.sql.orm.annoation.Table;
import scw.sql.orm.auto.AutoCreateService;
import scw.sql.orm.auto.CurrentTimeMillisAutoCreateService;
import scw.sql.orm.mysql.MysqlSelect;
import scw.sql.orm.result.DefaultResult;
import scw.sql.orm.result.DefaultResultSet;
import scw.sql.orm.result.Result;
import scw.sql.orm.result.ResultSet;
import scw.transaction.cache.QueryCacheUtils;

public abstract class AbstractORMTemplate extends SqlTemplate implements ORMOperations, SelectMaxId {
	@AutoCreate
	private BeanFactory beanFactory;
	private Map<String, AutoCreateService> autoCreateMap = new HashMap<String, AutoCreateService>();

	{
		setAutoCreateService("cts", CurrentTimeMillisAutoCreateService.CURRENT_TIME_MILLIS);
		setAutoCreateService("createTime", CurrentTimeMillisAutoCreateService.CURRENT_TIME_MILLIS);
	}

	protected synchronized void setAutoCreateService(String groupName, AutoCreateService autoCreateService) {
		autoCreateMap.put(groupName, autoCreateService);
	}

	protected AutoCreateService getAutoCreateService(String name) {
		AutoCreateService autoCreateService = autoCreateMap.get(name);
		if (autoCreateService == null && beanFactory != null) {
			autoCreateService = beanFactory.get(name);
		}
		return autoCreateService;
	}

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
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);

		for (ColumnInfo columnInfo : tableInfo.getAutoCreateColumns()) {
			AutoCreate autoCreate = columnInfo.getAutoCreate();
			String name = StringUtils.isEmpty(autoCreate.value()) ? columnInfo.getName() : autoCreate.value();
			AutoCreateService service = getAutoCreateService(name);
			if (service == null) {
				throw new NotFoundException(tableInfo.getClassInfo().getName() + "中字段[" + columnInfo.getName()
						+ "的注解@AutoCreate找不到指定名称的实现:" + name);
			}

			try {
				service.wrapper(this, bean, tableInfo, columnInfo, tName, autoCreate.args());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		Sql sql = getSqlFormat().toInsertSql(bean, tableInfo, tName);
		if (tableInfo.getAutoIncrement() == null) {
			return ormUpdateSql(tableInfo, tName, sql);
		} else {
			Connection connection = null;
			try {
				connection = getUserConnection();
				boolean b = update(sql, connection) != 0;
				if (!b) {
					return false;
				}

				Object lastId = query(getSqlFormat().toLastInsertIdSql(tName), connection,
						new ResultSetMapper<Object>() {

							public Object mapper(java.sql.ResultSet resultSet) throws SQLException {
								if (resultSet.next()) {
									return resultSet.getObject(1);
								}
								return null;
							}
						});
				tableInfo.getAutoIncrement().setValueToField(bean, lastId);
				return true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				close(connection);
			}
		}
	}

	public boolean update(Object bean, String tableName) {
		if (bean instanceof BeanFieldListen) {
			if (((BeanFieldListen) bean).get_field_change_map() == null) {
				return true;
			}
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		Sql sql = getSqlFormat().toUpdateSql(bean, tableInfo, tName);
		return ormUpdateSql(tableInfo, tName, sql);
	}

	public boolean delete(Object bean) {
		return delete(bean, null);
	}

	public boolean delete(Object bean, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		Sql sql = getSqlFormat().toDeleteSql(bean, tableInfo, tName);
		return ormUpdateSql(tableInfo, tName, sql);
	}

	public boolean deleteById(Class<?> type, Object... params) {
		return deleteById(null, type, params);
	}

	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		TableInfo tableInfo = getAndCheckPrimaryKey(type, params.length);
		String tName = getTableName(tableName, tableInfo);
		Sql sql = getSqlFormat().toDeleteByIdSql(tableInfo, tName, params);
		return ormUpdateSql(tableInfo, tName, sql);
	}

	public boolean saveOrUpdate(Object bean, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		Sql sql = getSqlFormat().toSaveOrUpdateSql(bean, tableInfo, tName);
		return ormUpdateSql(tableInfo, tName, sql);
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

	protected boolean ormUpdateSql(TableInfo tableInfo, String tableName, Sql sql) {
		return update(sql) != 0;
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

	public void createDataBase(Connection connection, String databaseName, String charsetName, String collate) {

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

	/**
	 * 不推荐使用
	 * 
	 * @return
	 */
	public Select createSelect() {
		return new MysqlSelect(this);
	}

	public void iterator(Class<?> tableClass, Iterator<Result> iterator) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		iterator(getSqlFormat().toSelectByIdSql(tableInfo, tableInfo.getName(), null), iterator);
	}

	public void iterator(Sql sql, final Iterator<Result> iterator) {
		query(sql, new RowCallback() {

			public void processRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
				try {
					iterator.iterator(new DefaultResult(rs));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
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

	private TableInfo getAndCheckPrimaryKey(Class<?> clz, int primaryLength) {
		TableInfo tableInfo = ORMUtils.getTableInfo(clz);
		if (tableInfo.getPrimaryKeyColumns().length != primaryLength) {
			throw new ParameterException("主键数量和参数不一致:" + clz.getName());
		}
		return tableInfo;
	}

	private String getTableName(String tableName, TableInfo tableInfo) {
		return StringUtils.isEmpty(tableName) ? tableInfo.getName() : tableName;
	}
}
