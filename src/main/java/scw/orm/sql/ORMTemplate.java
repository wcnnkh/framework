package scw.orm.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.FieldSetterListen;
import scw.core.Pagination;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.IteratorCallback;
import scw.core.utils.StringUtils;
import scw.orm.IteratorMapping;
import scw.orm.MappingContext;
import scw.orm.ORMException;
import scw.orm.sql.annotation.Generator;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.dialect.MysqlSelect;
import scw.orm.sql.dialect.PaginationSql;
import scw.orm.sql.dialect.Select;
import scw.orm.sql.enums.OperationType;
import scw.orm.sql.support.DefaultResultMapping;
import scw.orm.sql.support.DefaultResultSet;
import scw.orm.sql.support.SqlORMUtils;
import scw.sql.ResultSetMapper;
import scw.sql.RowCallback;
import scw.sql.Sql;
import scw.sql.SqlTemplate;
import scw.sql.SqlUtils;

public abstract class ORMTemplate extends SqlTemplate implements ORMOperations {
	public abstract GeneratorService getGeneratorService();

	public SqlMapper getSqlMapper() {
		return getSqlDialect().getSqlMapper();
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

		String tName = getSqlDialect().getTableName(type, tableName);
		ResultSet resultSet = select(getSqlDialect().toSelectByIdSql(type, tName, params));
		return resultSet.getFirst().get(type, tName);
	}

	public <T> List<T> getByIdList(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		String tName = getSqlDialect().getTableName(type, tableName);
		ResultSet resultSet = select(getSqlDialect().toSelectByIdSql(type, tName, params));
		return resultSet.getList(type, tName);
	}

	public Object getAutoIncrementLastId(Connection connection, String tableName) throws SQLException {
		return query(getSqlDialect().toLastInsertIdSql(tableName), connection, new ResultSetMapper<Object>() {

			public Object mapper(java.sql.ResultSet resultSet) throws SQLException {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}
		});
	}

	protected void generator(OperationType operationType, Class<?> clazz, Object bean, String tableName) {
		final GeneratorContext generatorContext = new GeneratorContext(this, operationType, bean,
				getSqlDialect().getSqlMapper(), tableName);
		getSqlMapper().iterator(null, clazz, new IteratorMapping<SqlMapper>() {

			public void iterator(MappingContext context, SqlMapper sqlMapper) throws ORMException {
				Generator generator = context.getColumn().getAnnotation(Generator.class);
				if (generator == null) {
					return;
				}

				generatorContext.setMappingContext(context);
				getGeneratorService().process(generatorContext);
			}
		});
	}

	protected boolean orm(OperationType operationType, Class<?> clazz, Object bean, String tableName) {
		generator(operationType, clazz, bean, tableName);
		String tName = getSqlDialect().getTableName(clazz, bean, tableName);
		Sql sql = SqlORMUtils.toSql(operationType, getSqlDialect(), clazz, bean, tName);
		Connection connection = null;
		try {
			connection = getUserConnection();
			return ormExecute(operationType, clazz, bean, tName, sql, connection);
		} catch (Throwable e) {
			throw new ORMException(SqlUtils.getSqlId(sql), e);
		} finally {
			close(connection);
		}
	}

	protected boolean ormExecute(final OperationType operationType, Class<?> clazz, final Object bean,
			final String tableName, final Sql sql, final Connection connection) throws Throwable {
		final int count = update(sql, connection);
		getSqlMapper().iterator(null, clazz, new IteratorMapping<SqlMapper>() {

			public void iterator(MappingContext context, SqlMapper mappingOperations) throws ORMException {
				if (mappingOperations.isAutoIncrement(context)) {
					if (operationType == OperationType.SAVE || operationType == OperationType.SAVE_OR_UPDATE) {
						if (count == 0) {
							logger.warn("执行{{}}更新行数为0，无法获取到主键自增编号", SqlUtils.getSqlId(sql));
						} else if (count == 1) {
							try {
								mappingOperations.setter(context, bean, getAutoIncrementLastId(connection, tableName));
							} catch (SQLException e) {
								throw new ORMException(context.getColumn().getName(), e);
							}
						}
					}
				}
			}
		});
		return count != 0;
	}

	public boolean save(Object bean, String tableName) {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		return orm(OperationType.SAVE, userClass, bean, getSqlDialect().getTableName(userClass, bean, tableName));
	}

	public boolean update(Object bean, String tableName) {
		if (bean instanceof FieldSetterListen) {
			if (((FieldSetterListen) bean).get_field_setter_map() == null) {
				logger.warn("更新对象[{}]不存在数据变更", bean.getClass().getName());
				return false;
			}
		}

		Class<?> userClass = ClassUtils.getUserClass(bean);
		return orm(OperationType.UPDATE, userClass, bean, getSqlDialect().getTableName(userClass, bean, tableName));
	}

	public boolean delete(Object bean) {
		return delete(bean, null);
	}

	public boolean delete(Object bean, String tableName) {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		return orm(OperationType.DELETE, userClass, bean, tableName);
	}

	public boolean deleteById(Class<?> type, Object... params) {
		return deleteById(null, type, params);
	}

	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		Sql sql = getSqlDialect().toDeleteByIdSql(type,
				StringUtils.isEmpty(tableName) ? getSqlMapper().getTableName(type) : tableName, params);
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

	public boolean saveOrUpdate(Object bean, String tableName) {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		return orm(OperationType.SAVE_OR_UPDATE, userClass, bean,
				getSqlDialect().getTableName(userClass, bean, tableName));
	}

	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inPrimaryKeys,
			Object... primaryKeys) {
		if (CollectionUtils.isEmpty(inPrimaryKeys)) {
			return Collections.EMPTY_MAP;
		}

		if (type == null) {
			throw new NullPointerException("type is null");
		}

		if (primaryKeys != null && primaryKeys.length > getSqlMapper().getPrimaryKeys(type).size() - 1) {
			throw new NullPointerException("primaryKeys length  greater than primary key lenght");
		}

		String tName = getSqlDialect().getTableName(type, tableName);
		Sql sql = getSqlDialect().toSelectInIdSql(type, tName, primaryKeys, inPrimaryKeys);
		ResultSet resultSet = select(sql);
		List<V> list = resultSet.getList(type, tName);
		if (list == null || list.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<String, K> keyMap = getSqlMapper().getInIdKeyMap(type, inPrimaryKeys, primaryKeys);
		Map<K, V> map = new HashMap<K, V>();
		for (V v : list) {
			String key = getSqlMapper().getObjectKey(type, v);
			map.put(keyMap.get(key), v);
		}
		return map;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIdList, Object... params) {
		return getInIdList(type, null, inIdList, params);
	}

	public ResultSet select(Sql sql) {
		return query(sql, new ResultSetMapper<ResultSet>() {

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
		createTable(tableClass, null);
	}

	public void createTable(Class<?> tableClass, String tableName) {
		execute(getSqlDialect().toCreateTableSql(tableClass, getSqlDialect().getTableName(tableClass, tableName)));
	}

	public void createTable(String packageName) {
		Collection<Class<?>> list = ClassUtils.getClassList(packageName, ClassUtils.getDefaultClassLoader());
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
		PaginationSql paginationSql = getSqlDialect().toPaginationSql(sql, page, limit);
		Long count = select(paginationSql.getCountSql()).getFirst().get(0);
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
		PaginationSql paginationSql = getSqlDialect().toPaginationSql(sql, page, limit);
		Long count = selectOne(Long.class, paginationSql.getCountSql());
		if (count == null) {
			count = 0L;
		}

		if (count == 0) {
			return new Pagination<ResultSet>(0, limit, ResultSet.EMPTY_RESULT_SET);
		}

		return new Pagination<ResultSet>(count, limit, select(paginationSql.getResultSql()));
	}

	public Pagination<ResultSet> select(int page, int limit, Sql sql) {
		return select((long) page, limit, sql);
	}

	/**
	 * 迭代所有的数据
	 * 
	 * @param tableClass
	 * @param iterator
	 */
	public <T> void iterator(final Class<T> tableClass, final IteratorCallback<T> iterator) {
		Sql sql = getSqlDialect().toSelectByIdSql(tableClass, getSqlMapper().getTableName(tableClass), null);
		iterator(sql, new IteratorCallback<ResultMapping>() {

			public boolean iteratorCallback(ResultMapping data) {
				T t = data.get(tableClass);
				if (t == null) {
					return true;
				}

				return iterator.iteratorCallback(t);
			}
		});
	}

	public <T> void iterator(Sql sql, final Class<T> type, final IteratorCallback<T> iterator) {
		iterator(sql, new IteratorCallback<ResultMapping>() {

			public boolean iteratorCallback(ResultMapping data) {
				T t = data.get(type);
				if (t == null) {
					return true;
				}

				return iterator.iteratorCallback(t);
			}
		});
	}

	public void iterator(Sql sql, final IteratorCallback<ResultMapping> iterator) {
		query(sql, new RowCallback() {

			public boolean processRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
				return iterator.iteratorCallback(new DefaultResultMapping(rs));
			}
		});
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String idField) {
		Sql sql = getSqlDialect().toMaxIdSql(tableClass, getSqlDialect().getTableName(tableClass, tableName), idField);
		return select(sql).getFirst().get(type, 0);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String idField) {
		return getMaxValue(type, tableClass, null, idField);
	}

	@Deprecated
	public Select createSelect() {
		return new MysqlSelect(this, getSqlDialect());
	}
}
