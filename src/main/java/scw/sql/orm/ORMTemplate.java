package scw.sql.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.FieldSetterListen;
import scw.core.Pagination;
import scw.core.exception.AlreadyExistsException;
import scw.core.exception.ParameterException;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.IteratorCallback;
import scw.core.utils.StringUtils;
import scw.sql.ResultSetMapper;
import scw.sql.RowCallback;
import scw.sql.Sql;
import scw.sql.SqlTemplate;
import scw.sql.SqlUtils;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.mysql.MysqlSelect;
import scw.sql.orm.result.DefaultResult;
import scw.sql.orm.result.DefaultResultSet;
import scw.sql.orm.result.Result;
import scw.sql.orm.result.ResultSet;
import scw.transaction.sql.cache.QueryCacheUtils;

public abstract class ORMTemplate extends SqlTemplate implements ORMOperations {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public abstract SqlFormat getSqlFormat();

	public final Logger getLogger() {
		return logger;
	}

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

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getDefaultName() : tableName;
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

		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getDefaultName() : tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectByIdSql(tableInfo, tName, params));
		return resultSet.getList(type, tName);
	}

	public boolean save(Object bean, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getName(bean) : tableName;

		Sql sql = getSqlFormat().toInsertSql(bean, tableInfo, tName);
		if (tableInfo.getAutoIncrement() == null) {
			return update(sql) != 0;
		} else {
			Connection connection = null;
			try {
				connection = getUserConnection();
				boolean b = update(sql, connection) != 0;
				if (!b) {
					getLogger().warn("执行{{}}更新行数为0，无法获取到主键自增编号", SqlUtils.getSqlId(sql));
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
				ORMUtils.set(tableInfo.getAutoIncrement().getField(), bean, lastId);
				return true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				close(connection);
			}
		}
	}

	public boolean update(Object bean, String tableName) {
		if (bean instanceof FieldSetterListen) {
			if (((FieldSetterListen) bean).get_field_setter_map() == null) {
				getLogger().warn("更新对象[{}]不存在数据变更", bean.getClass().getName());
				return false;
			}
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getName(bean) : tableName;
		Sql sql = getSqlFormat().toUpdateSql(bean, tableInfo, tName);
		return update(sql) != 0;
	}

	public boolean delete(Object bean) {
		return delete(bean, null);
	}

	public boolean delete(Object bean, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getName(bean) : tableName;
		Sql sql = getSqlFormat().toDeleteSql(bean, tableInfo, tName);
		return update(sql) != 0;
	}

	public boolean deleteById(Class<?> type, Object... params) {
		return deleteById(null, type, params);
	}

	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length != params.length) {
			throw new ParameterException("主键数量和参数不一致:" + type.getName());
		}

		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getDefaultName() : tableName;
		Sql sql = getSqlFormat().toDeleteByIdSql(tableInfo, tName, params);
		return update(sql) != 0;
	}

	public boolean saveOrUpdate(Object bean, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getName(bean) : tableName;
		Sql sql = getSqlFormat().toSaveOrUpdateSql(bean, tableInfo, tName);
		return update(sql) != 0;
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

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getDefaultName() : tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectInIdSql(tableInfo, tName, params, inIds));
		List<V> list = resultSet.getList(type, tName);
		if (list == null || list.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<K, V> map = new HashMap<K, V>();
		for (V v : list) {
			K k;
			try {
				k = (K) columnInfo.getField().get(v);
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

	public void createTable(Class<?> tableClass) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		createTable(tableClass, tableInfo.getDefaultName());
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

	/**
	 * 迭代所有的数据
	 * 
	 * @param tableClass
	 * @param iterator
	 */
	public <T> void iterator(final Class<T> tableClass, final IteratorCallback<T> iterator) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		Sql sql = getSqlFormat().toSelectByIdSql(tableInfo, tableInfo.getDefaultName(), null);
		iterator(sql, new IteratorCallback<Result>() {

			public boolean iteratorCallback(Result data) {
				T t = data.get(tableClass);
				if (t == null) {
					return true;
				}

				return iterator.iteratorCallback(t);
			}

		});
	}

	public void iterator(Sql sql, final IteratorCallback<Result> iterator) {
		query(sql, new RowCallback() {

			public boolean processRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
				return iterator.iteratorCallback(new DefaultResult(rs));
			}
		});
	}

	public <T> T getMaxValue(Class<?> tableClass, String tableName, String idField) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getDefaultName() : tableName;
		Sql sql = getSqlFormat().toMaxIdSql(tableInfo, tName, idField);
		return select(sql).getFirst().get(0);
	}

	public <T> T getMaxValue(Class<?> tableClass, String idField) {
		return getMaxValue(tableClass, null, idField);
	}
}
