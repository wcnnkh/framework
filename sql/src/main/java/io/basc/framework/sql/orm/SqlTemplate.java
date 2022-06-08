package io.basc.framework.sql.orm;

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

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.MaxValueFactory;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.Repository;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlException;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.util.Assert;
import io.basc.framework.util.page.PageSupport;
import io.basc.framework.util.page.Pagination;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.page.StreamPagination;
import io.basc.framework.util.page.StreamPaginations;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.Processor;

public interface SqlTemplate extends EntityOperations, SqlOperations, MaxValueFactory, Repository {
	/**
	 * 这里是将sql转为获取结果集的数量
	 * 
	 * @see SqlDialect#toCountSql(Sql)
	 * @param sql
	 * @return
	 */
	default long count(Sql sql) {
		Sql countSql = getMapper().toCountSql(sql);
		return queryFirst(long.class, countSql);
	}

	default void createTable(Class<?> entityClass) {
		createTable(entityClass, null);
	}

	default void createTable(Class<?> entityClass, @Nullable String tableName) {
		createTable(getMapper().getStructure(entityClass, null, tableName));
	}

	default void createTable(TableStructure tableStructure) {
		Collection<Sql> sqls = getMapper().createTable(tableStructure);
		try {
			process((conn) -> {
				for (Sql sql : sqls) {
					prepare(conn, sql).execute();
				}
			});
		} catch (SQLException e) {
			throw new SqlException(tableStructure.getSourceClass().getName(), e);
		}
	}

	@Override
	default <T> boolean delete(Class<? extends T> entityClass, T entity) {
		return delete(entityClass, entity, null);
	}

	default <T> boolean delete(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return delete(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	@Override
	default <E> long delete(Class<? extends E> entityClass, Conditions conditions) throws OrmException {
		return delete(getMapper().getStructure(entityClass), conditions);
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
		return query(connection, Object.class, sql).first();
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
		return queryFirst(tableStructure, sql);
	}

	default <T> List<T> getByIdList(Class<? extends T> entityClass, Object... ids) {
		return getByIdList(null, entityClass, ids);
	}

	default <T> List<T> getByIdList(@Nullable String tableName, Class<? extends T> entityClass, Object... ids) {
		return getByIdList(getMapper().getStructure(entityClass, null, tableName), ids);
	}

	default <T> List<T> getByIdList(TableStructure tableStructure, Object... ids) {
		Sql sql = getMapper().toSelectByIdsSql(tableStructure, ids);
		return queryAll(tableStructure, sql);
	}

	default <K, V> Map<K, V> getInIds(Class<? extends V> type, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		return getInIds(null, type, inPrimaryKeys, primaryKeys);
	}

	default <K, V> Map<K, V> getInIds(@Nullable String tableName, Class<? extends V> entityClass,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		return getInIds(getMapper().getStructure(entityClass, null, tableName), inPrimaryKeys, primaryKeys);
	}

	default <K, V> Map<K, V> getInIds(TableStructure tableStructure, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		Sql sql = getMapper().getInIds(tableStructure, primaryKeys, inPrimaryKeys);
		Cursor<V> cursor = query(tableStructure, sql);
		List<V> list = cursor.shared();
		if (list == null || list.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, K> keyMap = getObjectKeyFormat().getInIdsKeyMap(tableStructure, inPrimaryKeys, primaryKeys);
		Map<K, V> map = new LinkedHashMap<K, V>();
		for (V v : list) {
			String key = getObjectKeyFormat().getObjectKey(tableStructure, v);
			map.put(keyMap.get(key), v);
		}
		return map;
	}

	SqlDialect getMapper();

	/**
	 * 获取对象指定字段的最大值
	 * 
	 * @param            <T>
	 * @param type
	 * @param tableClass
	 * @param field
	 * @return
	 */
	@Nullable
	default <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, Field field) {
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
	default <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, @Nullable String tableName, Field field) {
		return getMaxValue(getMapper().getStructure(tableClass, null, tableName), type, field);
	}

	default <T> T getMaxValue(TableStructure tableStructure, Class<? extends T> type, Field field) {
		Sql sql = getMapper().toMaxIdSql(tableStructure, field);
		return queryFirst(type, sql);
	}

	ObjectKeyFormat getObjectKeyFormat();

	default <T> Pagination<T> getPage(Class<? extends T> resultType, Sql sql, long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, (rs) -> getMapper().convert(rs, resultType));
	}

	default <T> Pagination<T> getPage(Sql sql, long pageNumber, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		long start = PageSupport.getStart(pageNumber, limit);
		long total = count(sql);
		if (total == 0) {
			return PageSupport.emptyPagination(start, limit);
		}

		return new StreamPagination<T>(start, () -> limit(sql, start, limit, mapProcessor), limit, total);
	}

	default <T> Pagination<T> getPage(TableStructure tableStructure, Sql sql, long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, (rs) -> getMapper().convert(rs, tableStructure));
	}

	default <T> Pagination<T> getPage(TypeDescriptor resultType, Sql sql, long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, (rs) -> getMapper().convert(rs, resultType));
	}

	default <T> Paginations<T> getPages(Class<? extends T> resultType, Sql sql, long pageNumber, int limit) {
		return getPages(sql, pageNumber, limit, (rs) -> getMapper().convert(rs, resultType));
	}

	default <T> Paginations<T> getPages(Class<? extends T> queryClass, T query, long getNumber, long limit) {
		return getPages(getMapper().getStructure(queryClass, query, null), query, getNumber, limit);
	}

	default <T> Paginations<T> getPages(Sql sql, long pageNumber, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		long start = PageSupport.getStart(pageNumber, limit);
		long total = count(sql);
		if (total == 0) {
			return PageSupport.emptyPaginations(start, limit);
		}

		return new StreamPaginations<T>(total, start, limit, (begin, count) -> limit(sql, begin, count, mapProcessor));
	}

	default <T> Paginations<T> getPages(TableStructure tableStructure, T query, long getNumber, long limit) {
		Sql sql = getMapper().toQuerySql(tableStructure, query);
		return getPages(sql, getNumber, limit, (rs) -> getMapper().convert(rs, tableStructure));
	}

	default <T> Paginations<T> getPages(TypeDescriptor resultType, Sql sql, long pageNumber, long limit) {
		return getPages(sql, pageNumber, limit, (rs) -> getMapper().convert(rs, resultType));
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
	default TableChanges getTableChanges(Class<?> tableClass, @Nullable String tableName) {
		return getTableChanges(getMapper().getStructure(tableClass, null, tableName));
	}

	default TableChanges getTableChanges(TableStructure tableStructure) {
		TableStructureMapping tableStructureMapping = getMapper().getTableStructureMapping(tableStructure);
		List<Column> list = prepare(tableStructureMapping.getSql()).query().process((rs, rowNum) -> {
			return tableStructureMapping.getName(rs);
		});
		HashSet<String> hashSet = new HashSet<String>();
		List<String> deleteList = new ArrayList<String>();
		TableStructure oldStructure = getMapper().getStructure(tableStructure.getSourceClass());
		for (Column column : list) {
			hashSet.add(column.getName());
			Column oldName = oldStructure.getByName(column.getName(), null);
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

	default <T> boolean isPresentAny(TableStructure structure, T entity) {
		Sql sql = getMapper().toQuerySql(structure, entity);
		return limit(structure, sql, 0, 1).findAny().isPresent();
	}

	default <T> Cursor<T> limit(Class<? extends T> type, Sql sql, long start, long limit) {
		return limit(sql, start, limit, (rs) -> getMapper().convert(rs, type));
	}

	default <T> Cursor<T> limit(Sql sql, long start, long limit,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		Sql limitSql = getMapper().toLimitSql(sql, start, limit);
		return prepare(limitSql).query().stream(processor);
	}

	default <T> Cursor<T> limit(TableStructure structure, Sql sql, long start, long limit) {
		return limit(sql, start, limit, (rs) -> getMapper().convert(rs, structure));
	}

	default <T> Cursor<T> limit(TypeDescriptor resultsTypeDescriptor, Sql sql, long start, long limit) {
		return limit(sql, start, limit, (rs) -> getMapper().convert(rs, resultsTypeDescriptor));
	}

	@Override
	default <T, E> Paginations<T> pagingQuery(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			Conditions conditions, List<? extends OrderColumn> orders, PageRequest pageRequest) throws OrmException {
		return pagingQuery(resultsTypeDescriptor, getMapper().getStructure(entityClass), conditions, orders,
				pageRequest);
	}

	default <T> Paginations<T> pagingQuery(TypeDescriptor resultsTypeDescriptor, TableStructure structure,
			Conditions conditions, List<? extends OrderColumn> orders, PageRequest pageRequest) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		if (orders != null) {
			orderColumns.addAll(orders);
		}

		PageRequest request = pageRequest;
		if (request == null) {
			request = PageRequest.getPageRequest();
		}

		if (request == null) {
			request = new PageRequest();
		}

		Sql sql = getMapper().toSelectSql(structure,
				getMapper().open(structure.getSourceClass(), conditions, orderColumns), orderColumns);
		return getPages(resultsTypeDescriptor, sql, request.getPageNum(), request.getPageSize());
	}

	default <T> Cursor<T> query(Class<? extends T> queryClass, T query) {
		return query(getMapper().getStructure(queryClass, query, null), query);
	}

	default <T> Cursor<T> query(TableStructure tableStructure, Sql sql) {
		return query(sql, (rs) -> getMapper().convert(rs, tableStructure));
	}

	default <T> Cursor<T> query(TableStructure tableStructure, T query) {
		Sql sql = getMapper().toQuerySql(tableStructure, query);
		return query(tableStructure, sql);
	}

	@Override
	default <T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass, Conditions conditions,
			List<? extends OrderColumn> orders, PageRequest pageRequest) throws OrmException {
		return query(resultsTypeDescriptor, getMapper().getStructure(entityClass), conditions, orders, pageRequest);
	}

	default <T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor, TableStructure structure, Conditions conditions,
			List<? extends OrderColumn> orders) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		if (orders != null) {
			orderColumns.addAll(orders);
		}

		Sql sql = getMapper().toSelectSql(structure,
				getMapper().open(structure.getSourceClass(), conditions, orderColumns), orderColumns);
		return query(resultsTypeDescriptor, sql);
	}

	default <T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor, TableStructure structure, Conditions conditions,
			List<? extends OrderColumn> orders, PageRequest pageRequest) throws OrmException {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>(8);
		if (orders != null) {
			orderColumns.addAll(orders);
		}

		PageRequest request = pageRequest;
		if (request == null) {
			request = PageRequest.getPageRequest();
		}

		Sql sql = getMapper().toSelectSql(structure,
				getMapper().open(structure.getSourceClass(), conditions, orderColumns), orderColumns);
		if (pageRequest == null) {
			return query(resultsTypeDescriptor, sql);
		}
		return limit(resultsTypeDescriptor, sql, pageRequest.getStart(), pageRequest.getPageSize());
	}

	default <T> List<T> queryAll(TableStructure tableStructure, Sql sql) {
		Cursor<T> cursor = query(tableStructure, sql);
		return cursor.shared();
	}

	@Override
	default <T, E> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			Conditions conditions, List<? extends OrderColumn> orderColumns) throws OrmException {
		return query(resultsTypeDescriptor, getMapper().getStructure(entityClass), conditions, orderColumns);
	}

	default <T> Cursor<T> queryByIndexs(Class<? extends T> queryClass, T query) {
		return queryByIndexs(getMapper().getStructure(queryClass, query, null), query);
	}

	default <T> Cursor<T> queryByIndexs(TableStructure tableStructure, T query) {
		Sql sql = getMapper().toQuerySqlByIndexs(tableStructure, query);
		return query(tableStructure, sql);
	}

	default <T> Cursor<T> queryByPrimaryKeys(Class<? extends T> queryClass, T query) {
		return queryByPrimaryKeys(getMapper().getStructure(queryClass, query, null), query);
	}

	default <T> Cursor<T> queryByPrimaryKeys(TableStructure tableStructure, T query) {
		Sql sql = getMapper().toQuerySqlByPrimaryKeys(tableStructure, query);
		return query(tableStructure, sql);
	}

	@Nullable
	default <T> T queryFirst(TableStructure tableStructure, Sql sql) {
		Cursor<T> cursor = query(tableStructure, sql);
		return cursor.first();
	}

	@Override
	default <T> void save(Class<? extends T> entityClass, T entity) {
		save(entityClass, entity, null);
	}

	default <T> void save(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		save(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	@Override
	default <E> long save(Class<? extends E> entityClass, Collection<? extends Parameter> columns)
			throws OrmException {
		return save(getMapper().getStructure(entityClass), columns);
	}

	default long save(TableStructure structure, Collection<? extends Parameter> requestColumns)
			throws OrmException {
		Sql sql = getMapper().toSaveSql(structure, getMapper().open(structure.getSourceClass(), requestColumns, null));
		return update(sql);
	}

	default <T> void save(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");

		Sql sql = getMapper().toSaveSql(tableStructure, entity);
		prepare(sql).process((ps) -> {
			long updateCount = ps.executeUpdate();
			setAutoIncrementLastId(ps.getConnection(), tableStructure, entity, updateCount);
			return updateCount;
		});
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
		return prepare(sql).process((ps) -> {
			long updateCount = ps.executeUpdate();
			setAutoIncrementLastId(ps.getConnection(), tableStructure, entity, updateCount);
			return updateCount;
		}) > 0;
	}

	@Override
	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		return saveOrUpdate(entityClass, entity, null);
	}

	/**
	 * @see #saveOrUpdate(TableStructure, Object)
	 * @param             <T>
	 * @param entityClass
	 * @param entity
	 * @param tableName
	 * @return
	 */
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
	default <T> boolean update(Class<? extends T> entityClass, T entity) {
		return update(entityClass, entity, null);
	}

	default <T> boolean update(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return update(getMapper().getStructure(entityClass, entity, tableName), entity);
	}

	@Override
	default <E> long update(Class<? extends E> entityClass, Collection<? extends Parameter> columns,
			Conditions conditions) throws OrmException {
		return update(getMapper().getStructure(entityClass), columns, conditions);
	}

	default long update(TableStructure structure, Collection<? extends Parameter> columns, Conditions conditions)
			throws OrmException {
		List<Parameter> repositoryColumns = new ArrayList<Parameter>();
		getMapper().open(structure.getSourceClass(), repositoryColumns, null);
		Sql sql = getMapper().toUpdateSql(structure, repositoryColumns,
				getMapper().open(structure.getSourceClass(), conditions, null));
		return update(sql);
	}

	/**
	 * jdbc的url需要加一个参数useAffectedRows=true，mysql默认是false，也就是说默认返回的是查找到的行数，
	 * 而不是最终变化的行数。
	 * 
	 * @param tableStructure
	 * @param entity
	 * @return
	 */
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
