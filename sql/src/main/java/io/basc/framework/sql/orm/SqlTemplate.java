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
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.MaxValueFactory;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.StructureRegistry;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlException;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.sql.orm.convert.EntityStructureMapProcessor;
import io.basc.framework.sql.orm.convert.SmartMapProcessor;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.page.Page;
import io.basc.framework.util.page.PageSupport;
import io.basc.framework.util.page.Pages;
import io.basc.framework.util.page.StreamPage;
import io.basc.framework.util.page.StreamPages;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.Processor;

public interface SqlTemplate extends EntityOperations, SqlOperations, MaxValueFactory {
	SqlDialect getSqlDialect();

	ObjectKeyFormat getObjectKeyFormat();

	StructureRegistry<TableStructure> getStructureRegistry();

	default TableStructure getTableStructure(Class<?> entityClass) {
		StructureRegistry<TableStructure> registry = getStructureRegistry();
		if (registry.isRegistry(entityClass)) {
			return registry.getStructure(entityClass);
		}
		return getSqlDialect().getStructure(entityClass);
	}

	default <T> TableStructure resolve(Class<? extends T> entityClass, @Nullable T entity, @Nullable String tableName) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		if (StringUtils.isNotEmpty(tableName)) {
			return getTableStructure(entityClass).rename(tableName);
		}

		TableStructure tableStructure = getTableStructure(entityClass);
		if (entity != null && entity instanceof TableName) {
			String entityName = ((TableName) entity).getTableName();
			if (StringUtils.isNotEmpty(entityName)) {
				tableStructure = tableStructure.rename(entityName);
			}
		}
		return tableStructure;
	}

	default void createTable(Class<?> entityClass) {
		createTable(entityClass, null);
	}

	default void createTable(Class<?> entityClass, @Nullable String tableName) {
		createTable(resolve(entityClass, null, tableName));
	}

	default void createTable(TableStructure tableStructure) {
		Collection<Sql> sqls = getSqlDialect().createTable(tableStructure);
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

	default Object getAutoIncrementLastId(Connection connection, TableStructure tableStructure) {
		Sql sql = getSqlDialect().toLastInsertIdSql(tableStructure);
		return query(connection, Object.class, sql).first();
	}

	default void setAutoIncrementLastId(Connection connection, TableStructure tableStructure, Object entity,
			int updateCount) {
		if (updateCount != 1) {
			return;
		}

		for (Column column : tableStructure) {
			if (column.isAutoIncrement() && column.getField() != null) {
				Object lastId = getAutoIncrementLastId(connection, tableStructure);
				column.getField().getSetter().set(entity, lastId, getSqlDialect().getConversionService());
			}
		}
	}

	@Override
	default <T> void save(Class<? extends T> entityClass, T entity) {
		save(entityClass, entity, null);
	}

	default <T> int save(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		return save(resolve(entityClass, entity, tableName), entity);
	}

	default <T> int save(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");

		Sql sql = getSqlDialect().toSaveSql(tableStructure, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(ps.getConnection(), tableStructure, entity, updateCount);
			return updateCount;
		});
	}

	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		return saveIfAbsent(entityClass, entity, null) > 0;
	}

	default <T> int saveIfAbsent(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return saveIfAbsent(resolve(entityClass, entity, tableName), entity);
	}

	default <T> int saveIfAbsent(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getSqlDialect().toSaveIfAbsentSql(tableStructure, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(ps.getConnection(), tableStructure, entity, updateCount);
			return updateCount;
		});
	}

	@Override
	default <T> boolean delete(Class<? extends T> entityClass, T entity) {
		return delete(entityClass, entity, null) > 0;
	}

	default <T> int delete(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return delete(resolve(entityClass, entity, tableName), entity);
	}

	default <T> int delete(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getSqlDialect().toDeleteSql(tableStructure, entity);
		return update(sql);
	}

	@Override
	default boolean deleteById(Class<?> entityClass, Object... ids) {
		return deleteById(null, entityClass, ids) > 0;
	}

	default int deleteById(@Nullable String tableName, Class<?> entityClass, Object... ids) {
		if (entityClass == null) {
			return 0;
		}
		return deleteById(resolve(entityClass, null, tableName), ids);
	}

	default int deleteById(TableStructure tableStructure, Object... ids) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Sql sql = getSqlDialect().toDeleteByIdSql(tableStructure, ids);
		return update(sql);
	}

	default boolean updatePart(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return updatePart(entity.getClass(), entity);
	}

	default <T> boolean updatePart(Class<? extends T> entityClass, T entity) {
		return updatePart(entityClass, entity, null) > 0;
	}

	default <T> int updatePart(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return updatePart(resolve(entityClass, entity, tableName), entity);
	}

	default int updatePart(TableStructure tableStructure, Object entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getSqlDialect().toUpdatePartSql(tableStructure, entity);
		return update(sql);
	}

	@Override
	default <T> boolean update(Class<? extends T> entityClass, T entity) {
		return update(entityClass, entity, null) > 0;
	}

	default <T> int update(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return update(resolve(entityClass, entity, tableName), entity);
	}

	/**
	 * jdbc的url需要加一个参数useAffectedRows=true，mysql默认是false，也就是说默认返回的是查找到的行数，
	 * 而不是最终变化的行数。
	 * 
	 * @param tableStructure
	 * @param entity
	 * @return
	 */
	default <T> int update(TableStructure tableStructure, T entity) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Sql sql = getSqlDialect().toUpdateSql(tableStructure, entity);
		return update(sql);
	}
	
	default <T> boolean update(Class<? extends T> entityClass, T entity, T condition) {
		return update(entityClass, entity, condition, null) > 0;
	}

	default <T> int update(Class<? extends T> entityClass, T entity, T condition, @Nullable String tableName) {
		return update(resolve(entityClass, entity, tableName), entity);
	}
	
	default <T> int update(TableStructure tableStructure, T entity, T condition) {
		Assert.requiredArgument(tableStructure != null, "tableStructure");
		Assert.requiredArgument(entity != null, "entity");
		Assert.requiredArgument(condition != null, "condition");
		Sql sql = getSqlDialect().toUpdateSql(tableStructure, entity, condition);
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
	default <T> int saveOrUpdate(Class<? extends T> entityClass, T entity, @Nullable String tableName) {
		return saveOrUpdate(resolve(entityClass, entity, tableName), entity);
	}

	default <T> int saveOrUpdate(TableStructure tableStructure, T entity) {
		int count = saveIfAbsent(tableStructure, entity);
		if (count > 0) {
			return count;
		}

		return update(tableStructure, entity);
	}

	@Nullable
	@Override
	default <T> T getById(Class<? extends T> entityClass, Object... ids) {
		return getById(null, entityClass, ids);
	}

	@Nullable
	default <T> T getById(@Nullable String tableName, Class<? extends T> entityClass, Object... ids) {
		return getById(resolve(entityClass, null, tableName), ids);
	}

	@Nullable
	default <T> T getById(TableStructure tableStructure, Object... ids) {
		Sql sql = getSqlDialect().toSelectByIdsSql(tableStructure, ids);
		return queryFirst(tableStructure, sql);
	}

	default <T> List<T> getByIdList(Class<? extends T> entityClass, Object... ids) {
		return getByIdList(null, entityClass, ids);
	}

	default <T> List<T> getByIdList(@Nullable String tableName, Class<? extends T> entityClass, Object... ids) {
		return getByIdList(resolve(entityClass, null, tableName), ids);
	}

	default <T> List<T> getByIdList(TableStructure tableStructure, Object... ids) {
		Sql sql = getSqlDialect().toSelectByIdsSql(tableStructure, ids);
		return queryAll(tableStructure, sql);
	}

	default <K, V> Map<K, V> getInIds(Class<? extends V> type, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		return getInIds(null, type, inPrimaryKeys, primaryKeys);
	}

	default <K, V> Map<K, V> getInIds(@Nullable String tableName, Class<? extends V> entityClass,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		return getInIds(resolve(entityClass, null, tableName), inPrimaryKeys, primaryKeys);
	}

	default <K, V> Map<K, V> getInIds(TableStructure tableStructure, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		Sql sql = getSqlDialect().getInIds(tableStructure, primaryKeys, inPrimaryKeys);
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

	@Override
	public default <T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(Class<? extends T> type) {
		if (getStructureRegistry().isRegistry(type)) {
			return getMapProcessor(getStructureRegistry().getStructure(type));
		}
		return SqlOperations.super.getMapProcessor(type);
	}

	@Override
	default <T> Processor<ResultSet, T, Throwable> getMapProcessor(TypeDescriptor type) {
		SmartMapProcessor<T> processor = new SmartMapProcessor<T>(getSqlDialect(),
				getSqlDialect().getConversionService(), type);
		processor.setMapper(getMapper());
		processor.setStructureRegistry(getStructureRegistry());
		return processor;
	}

	default <T> Processor<ResultSet, T, ? extends Throwable> getMapProcessor(TableStructure structure) {
		return new EntityStructureMapProcessor<T>(structure, getSqlDialect().getConversionService());
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
		return getTableChanges(resolve(tableClass, null, tableName));
	}

	default TableChanges getTableChanges(TableStructure tableStructure) {
		TableStructureMapping tableStructureMapping = getSqlDialect().getTableStructureMapping(tableStructure);
		List<ColumnMetadata> list = prepare(tableStructureMapping.getSql()).query().process((rs, rowNum) -> {
			return tableStructureMapping.getName(rs);
		});
		HashSet<String> hashSet = new HashSet<String>();
		List<String> deleteList = new ArrayList<String>();
		Fields fields = getSqlDialect().getFields(tableStructure.getEntityClass());
		for (ColumnMetadata columnDescriptor : list) {
			hashSet.add(columnDescriptor.getName());
			Field column = fields.find(columnDescriptor.getName(), null);
			if (column == null) {// 在现在的表结构中不存在，应该删除
				deleteList.add(columnDescriptor.getName());
			}
		}

		List<Field> addList = new ArrayList<Field>();
		for (Field column : getSqlDialect().getFields(tableStructure.getEntityClass())) {
			String name = getSqlDialect().getName(tableStructure.getEntityClass(), column.getGetter());
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
		return getMaxValue(resolve(tableClass, null, tableName), type, field);
	}

	default <T> T getMaxValue(TableStructure tableStructure, Class<? extends T> type, Field field) {
		Sql sql = getSqlDialect().toMaxIdSql(tableStructure, field);
		return queryFirst(type, sql);
	}

	@Nullable
	default <T> T queryFirst(TableStructure tableStructure, Sql sql) {
		Cursor<T> cursor = query(tableStructure, sql);
		return cursor.first();
	}

	@Nullable
	default <T> T queryFirst(Class<? extends T> resultType, Sql sql) {
		Cursor<T> cursor = query(resultType, sql);
		return cursor.first();
	}

	default <T> List<T> queryAll(TableStructure tableStructure, Sql sql) {
		Cursor<T> cursor = query(tableStructure, sql);
		return cursor.shared();
	}

	default <T> List<T> queryAll(Class<? extends T> resultType, Sql sql) {
		Cursor<T> cursor = query(resultType, sql);
		return cursor.shared();
	}

	default <T> Cursor<T> query(TableStructure tableStructure, Sql sql) {
		return query(sql, getMapProcessor(tableStructure));
	}

	default <T> Cursor<T> query(TableStructure tableStructure, T query) {
		Sql sql = getSqlDialect().query(tableStructure, query);
		return query(tableStructure, sql);
	}

	default <T> Cursor<T> query(Class<? extends T> queryClass, T query) {
		return query(resolve(queryClass, query, null), query);
	}

	default long count(Sql sql) {
		Sql countSql = getSqlDialect().toCountSql(sql);
		return queryFirst(long.class, countSql);
	}

	default <T> Cursor<T> limit(TableStructure structure, Sql sql, long start, long limit) {
		return limit(sql, start, limit, getMapProcessor(structure));
	}

	default <T> Cursor<T> limit(Class<? extends T> type, Sql sql, long start, long limit) {
		return limit(sql, start, limit, getMapProcessor(type));
	}

	default <T> Cursor<T> limit(Sql sql, long start, long limit,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		Sql limitSql = getSqlDialect().toLimitSql(sql, start, limit);
		return prepare(limitSql).query().stream(processor);
	}

	default <T> Page<T> getPage(TableStructure tableStructure, Sql sql, long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, getMapProcessor(tableStructure));
	}

	default <T> Page<T> getPage(TypeDescriptor resultType, Sql sql, long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Page<T> getPage(Class<? extends T> resultType, Sql sql, long pageNumber, long limit) {
		return getPage(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Page<T> getPage(Sql sql, long pageNumber, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		long start = PageSupport.getStart(pageNumber, limit);
		long total = count(sql);
		if (total == 0) {
			return PageSupport.emptyPage(pageNumber, limit);
		}

		return new StreamPage<T>(start, () -> limit(sql, start, limit, mapProcessor), limit, total);
	}

	default <T> Pages<T> getPages(TypeDescriptor resultType, Sql sql, long pageNumber, long limit) {
		return getPages(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Pages<T> getPages(Sql sql, long pageNumber, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		long start = PageSupport.getStart(pageNumber, limit);
		long total = count(sql);
		if (total == 0) {
			return PageSupport.emptyPages(pageNumber, limit);
		}

		return new StreamPages<>(total, start, limit, (begin, count) -> limit(sql, begin, count, mapProcessor));
	}

	default <T> Pages<T> getPages(Class<? extends T> resultType, Sql sql, long pageNumber, int limit) {
		return getPages(sql, pageNumber, limit, getMapProcessor(resultType));
	}

	default <T> Pages<T> getPages(TableStructure tableStructure, T query, long getNumber, long limit) {
		Sql sql = getSqlDialect().query(tableStructure, query);
		return getPages(sql, getNumber, limit, getMapProcessor(tableStructure));
	}

	default <T> Pages<T> getPages(Class<? extends T> queryClass, T query, long getNumber, long limit) {
		return getPages(resolve(queryClass, query, null), query, getNumber, limit);
	}
}
