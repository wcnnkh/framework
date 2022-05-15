package io.basc.framework.sql.orm;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.MaxValueFactory;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.Repository;
import io.basc.framework.orm.repository.RepositoryColumn;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlException;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.sql.orm.convert.EntityStructureMapProcessor;
import io.basc.framework.sql.orm.convert.SmartMapProcessor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.page.PageSupport;
import io.basc.framework.util.page.Pagination;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.page.StreamPagination;
import io.basc.framework.util.page.StreamPaginations;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.Processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface SqlTemplate extends EntityOperations, SqlOperations,
		MaxValueFactory, Repository {
	SqlDialect getMapping();

	ObjectKeyFormat getObjectKeyFormat();

	default void createTable(Class<?> entityClass) {
		createTable(entityClass, null);
	}

	default void createTable(Class<?> entityClass, @Nullable String tableName) {
		createTable(getMapping().getStructure(entityClass, null, tableName));
	}

	default void createTable(TableStructure tableStructure) {
		Collection<Sql> sqls = getMapping().createTable(tableStructure);
		try {
			process((conn) -> {
				for (Sql sql : sqls) {
					prepare(conn, sql).execute();
				}
			});
		} catch (SQLException e) {
			throw new SqlException(tableStructure.getEntityClass().getName(), e);
		}
	}

	default Object getAutoIncrementLastId(Connection connection,
			TableStructure tableStructure) {
		Sql sql = getMapping().toLastInsertIdSql(tableStructure);
		return query(connection, Object.class, sql).first();
	}

	default void setAutoIncrementLastId(Connection connection,
			TableStructure tableStructure, Object entity, long updateCount) {
		if (updateCount != 1) {
			return;
		}

		for (Column column : tableStructure) {
			if (column.isAutoIncrement() && column.getField() != null) {
				Object lastId = getAutoIncrementLastId(connection,
						tableStructure);
				column.getField()
						.getSetter()
						.set(entity,
								lastId,
								getMapping().getEnvironment()
										.getConversionService());
			}
		}
	}

	@Override
	default <T> void save(Class<? extends T> entityClass, T entity) {
		save(entityClass, entity, null);
	}

	default <T> long save(Class<? extends T> entityClass, T entity,
			@Nullable String tableName) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		return save(getMapping().getStructure(entityClass, entity, tableName),
				entity);
	}

	default <T> long save(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");

		Sql sql = getMapping().toSaveSql(tableStructure, entity);
		return prepare(sql).process(
				(ps) -> {
					long updateCount = ps.executeUpdate();
					setAutoIncrementLastId(ps.getConnection(), tableStructure,
							entity, updateCount);
					return updateCount;
				});
	}

	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		return saveIfAbsent(entityClass, entity, null) > 0;
	}

	default <T> long saveIfAbsent(Class<? extends T> entityClass, T entity,
			@Nullable String tableName) {
		return saveIfAbsent(
				getMapping().getStructure(entityClass, entity, tableName),
				entity);
	}

	default <T> long saveIfAbsent(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapping().toSaveIfAbsentSql(tableStructure, entity);
		return prepare(sql).process(
				(ps) -> {
					long updateCount = ps.executeUpdate();
					setAutoIncrementLastId(ps.getConnection(), tableStructure,
							entity, updateCount);
					return updateCount;
				});
	}

	@Override
	default <T> boolean delete(Class<? extends T> entityClass, T entity) {
		return delete(entityClass, entity, null) > 0;
	}

	default <T> long delete(Class<? extends T> entityClass, T entity,
			@Nullable String tableName) {
		return delete(
				getMapping().getStructure(entityClass, entity, tableName),
				entity);
	}

	default <T> long delete(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapping().toDeleteSql(tableStructure, entity);
		return update(sql);
	}

	@Override
	default boolean deleteById(Class<?> entityClass, Object... ids) {
		return deleteById(null, entityClass, ids) > 0;
	}

	default long deleteById(@Nullable String tableName, Class<?> entityClass,
			Object... ids) {
		if (entityClass == null) {
			return 0;
		}
		return deleteById(
				getMapping().getStructure(entityClass, null, tableName), ids);
	}

	default long deleteById(TableStructure tableStructure, Object... ids) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Sql sql = getMapping().toDeleteByIdSql(tableStructure, ids);
		return update(sql);
	}

	default boolean updatePart(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return updatePart(entity.getClass(), entity);
	}

	default <T> boolean updatePart(Class<? extends T> entityClass, T entity) {
		return updatePart(entityClass, entity, null) > 0;
	}

	default <T> long updatePart(Class<? extends T> entityClass, T entity,
			@Nullable String tableName) {
		return updatePart(
				getMapping().getStructure(entityClass, entity, tableName),
				entity);
	}

	default long updatePart(TableStructure tableStructure, Object entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapping().toUpdatePartSql(tableStructure, entity);
		return update(sql);
	}

	@Override
	default <T> boolean update(Class<? extends T> entityClass, T entity) {
		return update(entityClass, entity, null) > 0;
	}

	default <T> long update(Class<? extends T> entityClass, T entity,
			@Nullable String tableName) {
		return update(
				getMapping().getStructure(entityClass, entity, tableName),
				entity);
	}

	/**
	 * jdbc的url需要加一个参数useAffectedRows=true，mysql默认是false，也就是说默认返回的是查找到的行数，
	 * 而不是最终变化的行数。
	 * 
	 * @param tableStructure
	 * @param entity
	 * @return
	 */
	default <T> long update(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getMapping().toUpdateSql(tableStructure, entity);
		return update(sql);
	}

	@Override
	default <T> long updateAll(Class<? extends T> entityClass, T entity,
			T oldEntity) {
		return updateAll(entityClass, entity, oldEntity, null);
	}

	default <T> long updateAll(Class<? extends T> entityClass, T entity,
			T oldEntity, @Nullable String tableName) {
		return updateAll(
				getMapping().getStructure(entityClass, entity, tableName),
				entity, oldEntity);
	}

	default <T> long updateAll(TableStructure tableStructure, T entity,
			T oldEntity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Assert.requiredArgument(oldEntity != null, "condition");
		Sql sql = getMapping().toUpdateSql(tableStructure, entity, oldEntity);
		return update(sql);
	}

	@Override
	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		return saveOrUpdate(entityClass, entity, null) > 0;
	}

	/**
	 * @see #saveOrUpdate(TableStructure, Object)
	 * @param <T>
	 * @param entityClass
	 * @param entity
	 * @param tableName
	 * @return
	 */
	default <T> long saveOrUpdate(Class<? extends T> entityClass, T entity,
			@Nullable String tableName) {
		return saveOrUpdate(
				getMapping().getStructure(entityClass, entity, tableName),
				entity);
	}

	default <T> long saveOrUpdate(TableStructure tableStructure, T entity) {
		long count = saveIfAbsent(tableStructure, entity);
		if (count > 0) {
			return count;
		}

		return update(tableStructure, entity);
	}

	@Override
	default <T> T getById(Class<? extends T> entityClass, Object... ids) {
		return getById(getMapping().getStructure(entityClass), ids);
	}

	@Nullable
	default <T> T getById(@Nullable String tableName,
			Class<? extends T> entityClass, Object... ids) {
		return getById(getMapping().getStructure(entityClass, null, tableName),
				ids);
	}

	@Nullable
	default <T> T getById(TableStructure tableStructure, Object... ids) {
		Sql sql = getMapping().toSelectByIdsSql(tableStructure, ids);
		return queryFirst(tableStructure, sql);
	}

	default <T> List<T> getByIdList(Class<? extends T> entityClass,
			Object... ids) {
		return getByIdList(null, entityClass, ids);
	}

	default <T> List<T> getByIdList(@Nullable String tableName,
			Class<? extends T> entityClass, Object... ids) {
		return getByIdList(
				getMapping().getStructure(entityClass, null, tableName), ids);
	}

	default <T> List<T> getByIdList(TableStructure tableStructure,
			Object... ids) {
		Sql sql = getMapping().toSelectByIdsSql(tableStructure, ids);
		return queryAll(tableStructure, sql);
	}

	default <K, V> Map<K, V> getInIds(Class<? extends V> type,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		return getInIds(null, type, inPrimaryKeys, primaryKeys);
	}

	default <K, V> Map<K, V> getInIds(@Nullable String tableName,
			Class<? extends V> entityClass,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		return getInIds(
				getMapping().getStructure(entityClass, null, tableName),
				inPrimaryKeys, primaryKeys);
	}

	default <K, V> Map<K, V> getInIds(TableStructure tableStructure,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		Sql sql = getMapping().getInIds(tableStructure, primaryKeys,
				inPrimaryKeys);
		Cursor<V> cursor = query(tableStructure, sql);
		List<V> list = cursor.shared();
		if (list == null || list.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, K> keyMap = getObjectKeyFormat().getInIdsKeyMap(
				tableStructure, inPrimaryKeys, primaryKeys);
		Map<K, V> map = new LinkedHashMap<K, V>();
		for (V v : list) {
			String key = getObjectKeyFormat().getObjectKey(tableStructure, v);
			map.put(keyMap.get(key), v);
		}
		return map;
	}

	@Override
	public default <T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(
			Class<? extends T> type) {
		if (getMapping().isRegistry(type)) {
			return getMapProcessor(getMapping().getStructure(type));
		}

		if (getMapper().isRegistred(type)) {
			return getMapper().getProcessor(type);
		}

		if (getMapping().isEntity(type)) {
			TableStructure tableStructure = getMapping().getStructure(type,
					null, null);
			return getMapProcessor(tableStructure);
		}

		return SqlOperations.super.getMapProcessor(type);
	}

	@Override
	default <T> Processor<ResultSet, T, Throwable> getMapProcessor(
			TypeDescriptor type) {
		SmartMapProcessor<T> processor = new SmartMapProcessor<T>(getMapping(),
				getMapping().getEnvironment().getConversionService(), type);
		processor.setMapper(getMapper());
		processor.setStructureRegistry(getMapping());
		return processor;
	}

	default <T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(
			TableStructure structure) {
		return new EntityStructureMapProcessor<T>(structure, getMapping()
				.getEnvironment().getConversionService());
	}

	/**
	 * 获取表的变更
	 * 
	 * @param tableClass
	 * @return
	 */
	default TableChanges getTableChanges(Class<?> tableClass) {
		return getTableChanges(tableClass, null);
	}

	/**
	 * 获取表的变更
	 * 
	 * @param tableClass
	 * @param tableName
	 * @return
	 */
	default TableChanges getTableChanges(Class<?> tableClass,
			@Nullable String tableName) {
		return getTableChanges(getMapping().getStructure(tableClass, null,
				tableName));
	}

	default TableChanges getTableChanges(TableStructure tableStructure) {
		TableStructureMapping tableStructureMapping = getMapping()
				.getTableStructureMapping(tableStructure);
		List<ColumnMetadata> list = prepare(tableStructureMapping.getSql())
				.query().process((rs, rowNum) -> {
					return tableStructureMapping.getName(rs);
				});
		HashSet<String> hashSet = new HashSet<String>();
		List<String> deleteList = new ArrayList<String>();
		Fields fields = getMapping().getFields(tableStructure.getEntityClass());
		for (ColumnMetadata columnDescriptor : list) {
			hashSet.add(columnDescriptor.getName());
			Field column = fields.find(columnDescriptor.getName(), null);
			if (column == null) {// 在现在的表结构中不存在，应该删除
				deleteList.add(columnDescriptor.getName());
			}
		}

		List<Field> addList = new ArrayList<Field>();
		for (Field column : getMapping().getFields(
				tableStructure.getEntityClass())) {
			String name = getMapping().getName(tableStructure.getEntityClass(),
					column.getGetter());
			if (!hashSet.contains(name)) {// 在已有的数据库中不存在，应该添加
				addList.add(column);
			}
		}
		return new TableChanges(deleteList, addList);
	}

	/**
	 * 获取对象指定字段的最大值
	 * 
	 * @param <T>
	 * @param type
	 * @param tableClass
	 * @param field
	 * @return
	 */
	@Nullable
	default <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass,
			Field field) {
		return getMaxValue(type, tableClass, null, field);
	}

	/**
	 * 获取对象指定字段的最大值
	 * 
	 * @param type
	 * @param tableClass
	 * @param tableName
	 * @param field
	 * @return
	 */
	@Nullable
	default <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass,
			@Nullable String tableName, Field field) {
		return getMaxValue(
				getMapping().getStructure(tableClass, null, tableName), type,
				field);
	}

	default <T> T getMaxValue(TableStructure tableStructure,
			Class<? extends T> type, Field field) {
		Sql sql = getMapping().toMaxIdSql(tableStructure, field);
		return queryFirst(type, sql);
	}

	@Nullable
	default <T> T queryFirst(TableStructure tableStructure, Sql sql) {
		Cursor<T> cursor = query(tableStructure, sql);
		return cursor.first();
	}

	default <T> List<T> queryAll(TableStructure tableStructure, Sql sql) {
		Cursor<T> cursor = query(tableStructure, sql);
		return cursor.shared();
	}

	default <T> Cursor<T> query(TableStructure tableStructure, Sql sql) {
		return query(sql, getMapProcessor(tableStructure));
	}

	default <T> Cursor<T> queryByPrimaryKeys(TableStructure tableStructure,
			T query) {
		Sql sql = getMapping().toQuerySqlByPrimaryKeys(tableStructure, query);
		return query(tableStructure, sql);
	}

	default <T> Cursor<T> queryByPrimaryKeys(Class<? extends T> queryClass,
			T query) {
		return queryByPrimaryKeys(
				getMapping().getStructure(queryClass, query, null), query);
	}

	default <T> Cursor<T> queryByIndexs(TableStructure tableStructure, T query) {
		Sql sql = getMapping().toQuerySqlByIndexs(tableStructure, query);
		return query(tableStructure, sql);
	}

	default <T> Cursor<T> queryByIndexs(Class<? extends T> queryClass, T query) {
		return queryByIndexs(
				getMapping().getStructure(queryClass, query, null), query);
	}

	default <T> Cursor<T> query(TableStructure tableStructure, T query) {
		Sql sql = getMapping().toQuerySql(tableStructure, query);
		return query(tableStructure, sql);
	}

	default <T> Cursor<T> query(Class<? extends T> queryClass, T query) {
		return query(getMapping().getStructure(queryClass, query, null), query);
	}

	/**
	 * 这里是将sql转为获取结果集的数量
	 * 
	 * @see SqlDialect#toCountSql(Sql)
	 * @param sql
	 * @return
	 */
	default long count(Sql sql) {
		Sql countSql = getMapping().toCountSql(sql);
		return queryFirst(long.class, countSql);
	}

	default <T> Cursor<T> limit(TableStructure structure, Sql sql, long start,
			long limit) {
		return limit(sql, start, limit, getMapProcessor(structure));
	}

	default <T> Cursor<T> limit(Class<? extends T> type, Sql sql, long start,
			long limit) {
		return limit(sql, start, limit, getMapProcessor(type));
	}

	default <T> Cursor<T> limit(Sql sql, long start, long limit,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		Sql limitSql = getMapping().toLimitSql(sql, start, limit);
		return prepare(limitSql).query().stream(processor);
	}

	default <T> Pagination<T> getPage(TableStructure tableStructure, Sql sql,
			long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, getMapProcessor(tableStructure));
	}

	default <T> Pagination<T> getPage(TypeDescriptor resultType, Sql sql,
			long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Pagination<T> getPage(Class<? extends T> resultType, Sql sql,
			long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Pagination<T> getPage(Sql sql, long pageNumber, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		long start = PageSupport.getStart(pageNumber, limit);
		long total = count(sql);
		if (total == 0) {
			return PageSupport.emptyPagination(start, limit);
		}

		return new StreamPagination<T>(start, () -> limit(sql, start, limit,
				mapProcessor), limit, total);
	}

	default <T> Paginations<T> getPages(TypeDescriptor resultType, Sql sql,
			long pageNumber, long limit) {
		return getPages(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Paginations<T> getPages(Sql sql, long pageNumber, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		long start = PageSupport.getStart(pageNumber, limit);
		long total = count(sql);
		if (total == 0) {
			return PageSupport.emptyPaginations(start, limit);
		}

		return new StreamPaginations<T>(total, start, limit,
				(begin, count) -> limit(sql, begin, count, mapProcessor));
	}

	default <T> Paginations<T> getPages(Class<? extends T> resultType, Sql sql,
			long pageNumber, int limit) {
		return getPages(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Paginations<T> getPages(TableStructure tableStructure, T query,
			long getNumber, long limit) {
		Sql sql = getMapping().toQuerySql(tableStructure, query);
		return getPages(sql, getNumber, limit, getMapProcessor(tableStructure));
	}

	default <T> Paginations<T> getPages(Class<? extends T> queryClass, T query,
			long getNumber, long limit) {
		return getPages(getMapping().getStructure(queryClass, query, null),
				query, getNumber, limit);
	}

	@Override
	default long save(Class<?> entityClass,
			Collection<? extends RepositoryColumn> columns) throws OrmException {
		return save(getMapping().getStructure(entityClass), columns);
	}

	default long save(TableStructure structure,
			Collection<? extends RepositoryColumn> requestColumns)
			throws OrmException {
		Sql sql = getMapping().toSaveSql(
				structure,
				getMapping().open(structure.getEntityClass(), requestColumns,
						null));
		return update(sql);
	}

	default long update(TableStructure structure,
			Collection<? extends RepositoryColumn> columns,
			Conditions conditions) throws OrmException {
		List<RepositoryColumn> repositoryColumns = new ArrayList<RepositoryColumn>();
		getMapping().open(structure.getEntityClass(), repositoryColumns, null);
		Sql sql = getMapping()
				.toUpdateSql(
						structure,
						repositoryColumns,
						getMapping().open(structure.getEntityClass(),
								conditions, null));
		return update(sql);
	}

	@Override
	default long update(Class<?> entityClass,
			Collection<? extends RepositoryColumn> columns,
			Conditions conditions) throws OrmException {
		return update(getMapping().getStructure(entityClass), columns,
				conditions);
	}

	default long delete(TableStructure structure, Conditions conditions)
			throws OrmException {
		Sql sql = getMapping()
				.toDeleteSql(
						structure,
						getMapping().open(structure.getEntityClass(),
								conditions, null));
		return update(sql);
	}

	@Override
	default long delete(Class<?> entityClass, Conditions conditions)
			throws OrmException {
		return delete(getMapping().getStructure(entityClass), conditions);
	}

	@Override
	default <T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor,
			Class<?> entityClass, Conditions conditions,
			List<? extends OrderColumn> orderColumns) throws OrmException {
		return query(resultsTypeDescriptor,
				getMapping().getStructure(entityClass), conditions,
				orderColumns);
	}

	default <T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor,
			TableStructure structure, Conditions conditions,
			List<? extends OrderColumn> orders) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		if (orders != null) {
			orderColumns.addAll(orders);
		}

		Sql sql = getMapping().toSelectSql(
				structure,
				getMapping().open(structure.getEntityClass(), conditions,
						orderColumns), orderColumns);
		return query(resultsTypeDescriptor, sql);
	}

	@Override
	default <T> Paginations<T> pagingQuery(
			TypeDescriptor resultsTypeDescriptor, Class<?> entityClass,
			Conditions conditions, List<? extends OrderColumn> orders,
			PageRequest pageRequest) throws OrmException {
		return pagingQuery(resultsTypeDescriptor,
				getMapping().getStructure(entityClass), conditions, orders,
				pageRequest);
	}

	default <T> Paginations<T> pagingQuery(
			TypeDescriptor resultsTypeDescriptor, TableStructure structure,
			Conditions conditions, List<? extends OrderColumn> orders,
			PageRequest pageRequest) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		if (orders != null) {
			orderColumns.addAll(orders);
		}

		Sql sql = getMapping().toSelectSql(
				structure,
				getMapping().open(structure.getEntityClass(), conditions,
						orderColumns), orderColumns);
		return getPages(resultsTypeDescriptor, sql, pageRequest.getPageNum(),
				pageRequest.getPageSize());
	}
}
