package io.basc.framework.orm.sql;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.MaxValueFactory;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.page.Page;
import io.basc.framework.util.page.PageSupport;
import io.basc.framework.util.page.Pages;
import io.basc.framework.util.page.StreamPages;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.Processor;

public interface SqlTemplate extends EntityOperations, SqlOperations, MaxValueFactory {
	SqlDialect getSqlDialect();

	default boolean createTable(Class<?> entityClass) {
		if (entityClass == null) {
			return false;
		}

		return createTable(null, entityClass);
	}

	default boolean createTable(@Nullable String tableName, Class<?> entityClass) {
		TableStructure tableStructure = getSqlDialect().resolve(entityClass);
		if (StringUtils.isNotEmpty(tableName)) {
			tableStructure = tableStructure.rename(tableName);
		}
		return createTable(tableStructure);
	}

	boolean createTable(TableStructure tableStructure);

	@Override
	default <T> void save(Class<? extends T> entityClass, T entity) {
		Assert.requiredArgument(entityClass != null, "entityClass");
		Assert.requiredArgument(entity != null, "entity");
		save(null, entityClass, entity);
	}

	<T> int save(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	<T> int save(TableStructure tableStructure, T entity);

	default <T> boolean saveIfAbsent(T entity) {
		if (entity == null) {
			return false;
		}

		return saveIfAbsent(getUserClass(entity.getClass()), entity);
	}

	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return saveIfAbsent(null, entityClass, entity);
	}

	<T> boolean saveIfAbsent(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	<T> boolean saveIfAbsent(TableStructure tableStructure, T entity);

	@Override
	default <T> boolean delete(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return delete(null, entityClass, entity) > 0;
	}

	<T> int delete(TableStructure tableStructure, T entity);

	<T> int delete(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	@Override
	default boolean deleteById(Class<?> entityClass, Object... ids) {
		if (entityClass == null) {
			return false;
		}

		return deleteById(null, entityClass, ids);
	}

	boolean deleteById(@Nullable String tableName, Class<?> entityClass, Object... ids);

	@Override
	default <T> boolean update(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return update(null, entityClass, entity) > 0;
	}

	<T> int update(TableStructure tableStructure, T entity);

	<T> int update(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	/**
	 * jdbc的url需要加一个参数useAffectedRows=true，mysql默认是false，也就是说默认返回的是查找到的行数，而不是最终变化的行数。
	 */
	@Override
	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		if (entityClass == null || entity == null) {
			return false;
		}

		return saveOrUpdate(null, entityClass, entity) > 0;
	}

	<T> int saveOrUpdate(TableStructure tableStructure, T entity);

	<T> int saveOrUpdate(@Nullable String tableName, Class<? extends T> entityClass, T entity);

	@Nullable
	@Override
	default <T> T getById(Class<? extends T> entityClass, Object... ids) {
		return getById(null, entityClass, ids);
	}

	@Nullable
	<T> T getById(@Nullable String tableName, Class<? extends T> entityClass, Object... ids);

	default <T> List<T> getByIdList(Class<? extends T> entityClass, Object... ids) {
		return getByIdList(null, entityClass, ids);
	}

	<T> List<T> getByIdList(@Nullable String tableName, Class<? extends T> entityClass, Object... ids);

	default <K, V> Map<K, V> getInIds(Class<? extends V> type, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		return getInIds(null, type, inPrimaryKeys, primaryKeys);
	}

	<K, V> Map<K, V> getInIds(String tableName, Class<? extends V> entityClass, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys);

	default <T> Page<T> getPage(TypeDescriptor resultType, Sql sql, long pageNumber, long limit) {
		Pages<T> pages = getPages(resultType, sql, pageNumber, limit);
		return pages.shared();
	}

	default <T> Page<T> getPage(Class<? extends T> resultType, Sql sql, long pageNumber, long limit) {
		return getPage(TypeDescriptor.valueOf(resultType), sql, pageNumber, limit);
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
		return getPages(TypeDescriptor.valueOf(resultType), sql, pageNumber, limit);
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
	TableChanges getTableChanges(Class<?> tableClass, @Nullable String tableName);

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
	<T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, @Nullable String tableName, Field field);

	default <T> Cursor<T> query(TableStructure tableStructure, Sql sql) {
		return query(sql, new TableStructureMapProcessor<T>(tableStructure));
	}

	default <T> Cursor<T> query(TableStructure tableStructure, T query) {
		Sql sql = getSqlDialect().query(tableStructure, query);
		return query(tableStructure, sql);
	}

	default <T> Cursor<T> query(Class<? extends T> queryClass, T query) {
		return query(getSqlDialect().resolve(queryClass), query);
	}

	default long count(Sql sql) {
		Sql countSql = getSqlDialect().toCountSql(sql);
		return query(long.class, countSql).first();
	}

	default <T> Cursor<T> limit(TableStructure structure, Sql sql, long start, long limit) {
		Sql limitSql = getSqlDialect().toLimitSql(sql, start, limit);
		return query(structure, limitSql);
	}

	default <T> Cursor<T> limit(Class<? extends T> type, Sql sql, long start, long limit) {
		Sql limitSql = getSqlDialect().toLimitSql(sql, start, limit);
		return query(type, limitSql);
	}

	default <T> Cursor<T> limit(Sql sql, long start, long limit,
			Processor<ResultSet, ? extends T, ? extends Throwable> processor) {
		Sql limitSql = getSqlDialect().toLimitSql(sql, start, limit);
		return prepare(limitSql).query().stream(processor);
	}

	default <T> Pages<T> getPages(TableStructure tableStructure, T query, long getNumber, long limit) {
		Sql sql = getSqlDialect().query(tableStructure, query);
		return getPages(sql, getNumber, limit, new TableStructureMapProcessor<T>(tableStructure));
	}

	default <T> Pages<T> getPages(Class<? extends T> queryClass, T query, long getNumber, long limit) {
		return getPages(getSqlDialect().resolve(queryClass), query, getNumber, limit);
	}
}
