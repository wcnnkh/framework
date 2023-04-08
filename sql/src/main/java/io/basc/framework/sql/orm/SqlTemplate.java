package io.basc.framework.sql.orm;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.MaxValueFactory;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.PrimaryKeyElements;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.Repository;
import io.basc.framework.sql.ConnectionOperations;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

@SuppressWarnings("unchecked")
public interface SqlTemplate extends EntityOperations, SqlOperations, MaxValueFactory, Repository {
	default long count(Sql sql) {
		Sql countSql = getMapper().toCountSql(sql);
		return query(long.class, countSql).getElements().first();
	}

	default void createTable(Class<?> entityClass) {
		createTable(entityClass, null);
	}

	default void createTable(Class<?> entityClass, @Nullable String tableName) {
		createTable(getMapper().getStructure(entityClass, null, tableName));
	}

	default void createTable(TableStructure tableStructure) {
		Collection<Sql> sqls = getMapper().createTable(tableStructure);
		for (Sql sql : sqls) {
			execute(sql);
		}
	}

	@Override
	default <E> long delete(Class<? extends E> entityClass, Conditions conditions) throws OrmException {
		return delete(getMapper().getStructure(entityClass), conditions);
	}

	@Override
	default <T> boolean delete(Class<? extends T> entityClass, T entity) {
		return delete(entityClass, entity, null);
	}

	default <T> boolean delete(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return delete(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	default long delete(TableStructure structure, Conditions conditions) throws OrmException {
		Sql sql = getMapper().toDeleteSql(structure, getMapper().open(structure.getSourceClass(), conditions, null));
		return update(sql);
	}

	default <T> boolean delete(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapper().toDeleteSql(tableStructure, entity);
		return update(sql) > 0;
	}

	default <T> long deleteAll(TableStructure structure, T conditions) {
		Sql sql = getMapper().toDeleteSql(structure, conditions);
		return update(sql);
	}

	@Override
	default <E> boolean deleteById(Class<? extends E> entityClass, Object... ids) {
		return deleteById(null, entityClass, ids) > 0;
	}

	default long deleteById(@Nullable String tableName, Class<?> entityClass, Object... ids) {
		if (entityClass == null) {
			return 0;
		}
		return deleteById(getMapper().getStructure(entityClass, null, tableName), ids);
	}

	default long deleteById(TableStructure tableStructure, Object... ids) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Sql sql = getMapper().toDeleteByIdSql(tableStructure, ids);
		return update(sql);
	}

	default Object getAutoIncrementLastId(Connection connection, TableStructure tableStructure) {
		Sql sql = getMapper().toLastInsertIdSql(tableStructure);
		return ConnectionOperations.of(connection).prepare(sql).query().rows((e) -> e.getObject(1)).first();
	}

	@Override
	default <T> T getById(Class<? extends T> entityClass, Object... ids) {
		return getById(getMapper().getStructure(entityClass), ids);
	}

	@Nullable
	default <T> T getById(@Nullable String tableName, Class<? extends T> entityClass, Object... ids) {
		return getById(getMapper().getStructure(entityClass, null, tableName), ids);
	}

	@Nullable
	default <T> T getById(TableStructure tableStructure, Object... ids) {
		Sql sql = getMapper().toSelectByIdsSql(tableStructure, ids);
		return (T) query(tableStructure, sql).getElements().first();
	}

	default <T> List<T> getByIdList(Class<? extends T> entityClass, Object... ids) {
		return getByIdList(null, entityClass, ids);
	}

	default <T> List<T> getByIdList(@Nullable String tableName, Class<? extends T> entityClass, Object... ids) {
		return getByIdList(getMapper().getStructure(entityClass, null, tableName), ids);
	}

	default <T> List<T> getByIdList(TableStructure tableStructure, Object... ids) {
		Sql sql = getMapper().toSelectByIdsSql(tableStructure, ids);
		return (List<T>) query(tableStructure, sql).getElements().toList();
	}

	@Override
	default <K, T> PrimaryKeyElements<K, T> getInIds(Class<? extends T> entityClass, List<? extends K> inPrimaryKeys,
			Object... primaryKeys) throws OrmException {
		return getInIds((String) null, entityClass, inPrimaryKeys, primaryKeys);
	}

	default <K, V> PrimaryKeyElements<K, V> getInIds(@Nullable String tableName, Class<? extends V> entityClass,
			List<? extends K> inPrimaryKeys, Object... primaryKeys) {
		return getInIds(getMapper().getStructure(entityClass, null, tableName), inPrimaryKeys, primaryKeys);
	}

	default <K, V> PrimaryKeyElements<K, V> getInIds(TableStructure tableStructure, List<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		Sql sql = getMapper().getInIds(tableStructure, primaryKeys, inPrimaryKeys);
		Query<V> resultSet = query(tableStructure, sql);
		return new PrimaryKeyElements<>(resultSet.getElements(), getObjectKeyFormat(), tableStructure, inPrimaryKeys,
				primaryKeys);
	}

	SqlDialect getMapper();

	@Nullable
	default <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, Field field) {
		return getMaxValue(type, tableClass, null, field);
	}

	@Nullable
	default <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, @Nullable String tableName, Field field) {
		return getMaxValue(getMapper().getStructure(tableClass, null, tableName), type, field);
	}

	default <T> T getMaxValue(TableStructure tableStructure, Class<? extends T> type, Field field) {
		Sql sql = getMapper().toMaxIdSql(tableStructure, field);
		return query(type, sql).getElements().first();
	}

	ObjectKeyFormat getObjectKeyFormat();

	default TableChanges getTableChanges(Class<?> tableClass) {
		return getTableChanges(tableClass, null);
	}

	default TableChanges getTableChanges(Class<?> tableClass, @Nullable String tableName) {
		return getTableChanges(getMapper().getStructure(tableClass, null, tableName));
	}

	default TableChanges getTableChanges(TableStructure tableStructure) {
		TableStructureMapping tableStructureMapping = getMapper().getTableStructureMapping(tableStructure);
		List<Column> list = query(tableStructureMapping.getSql(), (rs) -> tableStructureMapping.getColumn(rs)).toList();
		HashSet<String> hashSet = new HashSet<String>();
		List<String> deleteList = new ArrayList<String>();
		TableStructure oldStructure = getMapper().getStructure(tableStructure.getSourceClass());
		for (Column column : list) {
			hashSet.add(column.getName());
			Column oldName = oldStructure.getByName(column.getName());
			if (oldName == null) {// 在现在的表结构中不存在，应该删除
				deleteList.add(column.getName());
			}
		}

		List<Field> addList = new ArrayList<Field>();
		for (Field column : getMapper().getStructure(tableStructure.getSourceClass())) {
			String name = getMapper().getName(tableStructure.getSourceClass(), column.getGetter());
			if (!hashSet.contains(name)) {// 在已有的数据库中不存在，应该添加
				addList.add(column);
			}
		}
		return new TableChanges(deleteList, addList);
	}

	@Override
	default <T> Query<T> query(Class<? extends T> entityClass, Conditions conditions,
			List<? extends OrderColumn> orders) throws OrmException {
		return query(TypeDescriptor.valueOf(entityClass), entityClass, conditions, orders);
	}

	@Override
	default <T> Query<T> query(Class<? extends T> resultType, Sql sql) {
		return query(TypeDescriptor.valueOf(resultType), sql);
	}

	@Override
	default <T> Query<T> query(Class<? extends T> resultType, String sql) {
		return query(resultType, new SimpleSql(sql));
	}

	@Override
	default <T> Query<T> query(Class<? extends T> resultType, String sql, Object... sqlParams) {
		return query(resultType, new SimpleSql(sql, sqlParams));
	}

	default <T> Query<T> query(Class<? extends T> queryClass, T query) {
		return query(getMapper().getStructure(queryClass, query, null), query);
	}

	default <T> Query<T> query(TableStructure structure, Sql sql) {
		return new Query<>(this, getMapper(), sql, (rs) -> (T) getMapper().convert(rs, structure));
	}

	default <T> Query<T> query(TableStructure tableStructure, T query) {
		Sql sql = getMapper().toQuerySql(tableStructure, query);
		return new Query<>(this, getMapper(), sql, (rs) -> (T) getMapper().convert(rs, tableStructure));
	}

	@Override
	default <T, E> Query<T> query(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			Conditions conditions, List<? extends OrderColumn> orderColumns) throws OrmException {
		return query(resultsTypeDescriptor, getMapper().getStructure(entityClass), conditions, orderColumns);
	}

	@Override
	default <T, E> Query<T> query(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass, E conditions)
			throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		return query(resultsTypeDescriptor, entityClass,
				getMapper().parseConditions(entityClass, getMapper().getStructure(entityClass).columns().iterator(),
						orderColumns, (e) -> e.get(conditions), (e) -> StringUtils.isNotEmpty(e.getValue())),
				orderColumns);
	}

	@Override
	default <T> Query<T> query(TypeDescriptor resultType, Sql sql) {
		return new Query<>(this, getMapper(), sql, resultType);
	}

	@Override
	default <T> Query<T> query(TypeDescriptor resultType, String sql) {
		return query(resultType, new SimpleSql(sql));
	}

	@Override
	default <T> Query<T> query(TypeDescriptor resultType, String sql, Object... sqlParams) {
		return query(resultType, new SimpleSql(sql, sqlParams));
	}

	default <T> Query<T> query(TypeDescriptor resultsTypeDescriptor, TableStructure structure, Conditions conditions,
			List<? extends OrderColumn> orders) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		if (orders != null) {
			orderColumns.addAll(orders);
		}

		Sql sql = getMapper().toSelectSql(structure,
				getMapper().open(structure.getSourceClass(), conditions, orderColumns), orderColumns);
		return query(resultsTypeDescriptor, sql);
	}

	default <T> Query<T> queryByIndexs(Class<? extends T> queryClass, T query) {
		return queryByIndexs(getMapper().getStructure(queryClass, query, null), query);
	}

	default <T> Query<T> queryByIndexs(TableStructure tableStructure, T query) {
		Sql sql = getMapper().toQuerySqlByIndexs(tableStructure, query);
		return query(tableStructure, sql);
	}

	default <T> Query<T> queryByPrimaryKeys(Class<? extends T> queryClass, T query) {
		return queryByPrimaryKeys(getMapper().getStructure(queryClass, query, null), query);
	}

	default <T> Query<T> queryByPrimaryKeys(TableStructure tableStructure, T query) {
		Sql sql = getMapper().toQuerySqlByPrimaryKeys(tableStructure, query);
		return query(tableStructure, sql);
	}

	@Override
	default <T> void save(Class<? extends T> entityClass, T entity) {
		save(entityClass, entity, null);
	}

	default <T> void save(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		save(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	default <T> void save(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");

		Sql sql = getMapper().toSaveSql(tableStructure, entity);
		process(sql, (ps) -> {
			long updateCount = ps.executeUpdate();
			setAutoIncrementLastId(ps.getConnection(), tableStructure, entity, updateCount);
			return updateCount;
		});
	}

	@Override
	default <E> long saveColumns(Class<? extends E> entityClass, Collection<? extends Parameter> columns)
			throws OrmException {
		return saveColumns(getMapper().getStructure(entityClass), columns);
	}

	default int saveColumns(TableStructure structure, Collection<? extends Parameter> requestColumns)
			throws OrmException {
		Sql sql = getMapper().toSaveColumnsSql(structure,
				getMapper().open(structure.getSourceClass(), requestColumns, null));
		return update(sql);
	}

	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		return saveIfAbsent(entityClass, entity, null);
	}

	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return saveIfAbsent(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	default <T> boolean saveIfAbsent(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapper().toSaveIfAbsentSql(tableStructure, entity);
		return process(sql, (ps) -> {
			long updateCount = ps.executeUpdate();
			setAutoIncrementLastId(ps.getConnection(), tableStructure, entity, updateCount);
			return updateCount;
		}) > 0;
	}

	@Override
	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		return saveOrUpdate(entityClass, entity, null);
	}

	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return saveOrUpdate(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	default <T> boolean saveOrUpdate(TableStructure tableStructure, T entity) {
		if (saveIfAbsent(tableStructure, entity)) {
			return true;
		}
		return update(tableStructure, entity);
	}

	default void setAutoIncrementLastId(Connection connection, TableStructure tableStructure, Object entity,
			long updateCount) {
		if (updateCount != 1) {
			return;
		}

		for (Column column : tableStructure) {
			if (column.isAutoIncrement() && column.isSupportSetter()) {
				Object lastId = getAutoIncrementLastId(connection, tableStructure);
				column.getSetter().set(entity, lastId, getMapper().getEnvironment().getConversionService());
			}
		}
	}

	@Override
	default <E> long update(Class<? extends E> entityClass, Collection<? extends Parameter> columns,
			Conditions conditions) throws OrmException {
		return update(getMapper().getStructure(entityClass), columns, conditions);
	}

	@Override
	default <T> boolean update(Class<? extends T> entityClass, T entity) {
		return update(entityClass, entity, null);
	}

	default <T> boolean update(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return update(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	default long update(TableStructure structure, Collection<? extends Parameter> columns, Conditions conditions)
			throws OrmException {
		List<Parameter> repositoryColumns = new ArrayList<Parameter>();
		getMapper().open(structure.getSourceClass(), repositoryColumns, null);
		Sql sql = getMapper().toUpdateSql(structure, repositoryColumns,
				getMapper().open(structure.getSourceClass(), conditions, null));
		return update(sql);
	}

	default <T> boolean update(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapper().toUpdateSql(tableStructure, entity);
		return update(sql) > 0;
	}

	@Override
	default <T> long updateAll(Class<? extends T> entityClass, T entity, T oldEntity) {
		return updateAll(entityClass, entity, oldEntity, null);
	}

	default <T> long updateAll(Class<? extends T> entityClass, T entity, T oldEntity, @Nullable String tableName) {
		return updateAll(getMapper().getStructure(entityClass, entity, tableName), entity, oldEntity);
	}

	default <T> long updateAll(TableStructure tableStructure, T entity, T oldEntity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Assert.requiredArgument(oldEntity != null, "condition");
		Sql sql = getMapper().toUpdateSql(tableStructure, entity, oldEntity);
		return update(sql);
	}

	default <T> boolean updatePart(Class<? extends T> entityClass, T entity) {
		return updatePart(entityClass, entity, null) > 0;
	}

	default <T> long updatePart(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return updatePart(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	default boolean updatePart(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return updatePart(entity.getClass(), entity);
	}

	default long updatePart(TableStructure tableStructure, Object entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapper().toUpdatePartSql(tableStructure, entity);
		return update(sql);
	}
}
