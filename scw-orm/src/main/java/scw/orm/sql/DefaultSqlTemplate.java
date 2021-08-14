package scw.orm.sql;

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

import scw.aop.support.ProxyUtils;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.StringUtils;
import scw.env.Sys;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.mapper.MapProcessDecorator;
import scw.orm.sql.convert.SmartMapProcessor;
import scw.sql.ConnectionFactory;
import scw.sql.DefaultSqlOperations;
import scw.sql.Sql;
import scw.util.page.Page;
import scw.util.page.PageSupport;
import scw.util.page.Pages;
import scw.util.page.SharedPage;
import scw.util.stream.Cursor;
import scw.util.stream.Processor;

public class DefaultSqlTemplate extends DefaultSqlOperations implements
		SqlTemplate {
	private static Logger logger = LoggerFactory
			.getLogger(DefaultSqlTemplate.class);
	private final SqlDialect sqlDialect;
	private ConversionService conversionService;

	public DefaultSqlTemplate(ConnectionFactory connectionFactory,
			SqlDialect sqlDialect) {
		super(connectionFactory);
		this.sqlDialect = sqlDialect;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService()
				: conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	protected Class<?> getUserEntityClass(Class<?> entityClass) {
		return ProxyUtils.getFactory().getUserClass(entityClass);
	}

	protected <T> String getTableName(@Nullable String tableName,
			Class<? extends T> entityClass, @Nullable T entity) {
		if (StringUtils.isNotEmpty(tableName)) {
			return tableName;
		}

		String entityName = null;
		if (entity != null && entity instanceof TableName) {
			entityName = ((TableName) entity).getTableName();
		}

		if (StringUtils.isEmpty(entityName)) {
			entityName = sqlDialect.getName(entityClass);
		}
		return entityName;
	}

	@Override
	public boolean createTable(String tableName, Class<?> entityClass) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.toCreateTableSql(
				getTableName(tableName, clazz, null), clazz);
		prepare(sql).execute();
		return true;
	}

	private Object getAutoIncrementLastId(Connection connection,
			String tableName) throws SQLException {
		Sql sql = sqlDialect.toLastInsertIdSql(tableName);
		return query(connection, sql, (rs) -> rs.getObject(1)).first();
	}

	private void setAutoIncrementLastId(int updateCount, Sql sql,
			Connection connection, String tableName, Class<?> entityClass,
			Object entity) throws SQLException {
		for (Column column : sqlDialect.resolve(entityClass)) {
			if (column.isAutoIncrement() && column.getField() != null) {
				if (updateCount == 0) {
					logger.error("Number of rows affected is 0, execute: {}",
							sql);
				} else if (updateCount == 1) {
					Object lastId = getAutoIncrementLastId(connection,
							tableName);
					column.getField().getSetter()
							.set(entity, lastId, getConversionService());
				}
			}
		}
	}

	@Override
	public <T> boolean save(String tableName, Class<? extends T> entityClass,
			T entity) {
		String tName = getTableName(tableName, entityClass, entity);
		Sql sql = sqlDialect.save(tName, entityClass, entity);
		return prepare(sql).process(
				(ps) -> {
					int updateCount = ps.executeUpdate();
					setAutoIncrementLastId(updateCount, sql,
							ps.getConnection(), tName, entityClass, entity);
					return updateCount;
				}) > 0;
	}

	@Override
	public <T> boolean saveOrUpdate(String tableName,
			Class<? extends T> entityClass, T entity) {
		String tName = getTableName(tableName, entityClass, entity);
		Sql sql = sqlDialect.toSaveOrUpdateSql(tName, entityClass, entity);
		return prepare(sql).process(
				(ps) -> {
					int updateCount = ps.executeUpdate();
					setAutoIncrementLastId(updateCount, sql,
							ps.getConnection(), tName, entityClass, entity);
					return updateCount;
				}) > 0;
	}

	@Override
	public <T> boolean delete(String tableName, Class<? extends T> entityClass,
			T entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.delete(getTableName(tableName, clazz, entity),
				clazz, entity);
		return prepare(sql).update() > 0;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> entityClass,
			Object... ids) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.deleteById(getTableName(tableName, clazz, null),
				clazz, ids);
		return prepare(sql).update() > 0;
	}

	@Override
	public <T> boolean update(String tableName, Class<? extends T> entityClass,
			T entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.update(getTableName(tableName, clazz, entity),
				clazz, entity);
		return prepare(sql).update() > 0;
	}

	@Override
	public <T> T getById(String tableName, Class<? extends T> entityClass,
			Object... ids) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.toSelectByIdsSql(
				getTableName(tableName, clazz, null), clazz, ids);
		return query(entityClass, sql).first();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Processor<ResultSet, T, Throwable> getMapProcessor(
			TypeDescriptor type) {
		return new MapProcessDecorator<>(getMapper(),
				new SmartMapProcessor<>(sqlDialect, type),
				(Class<T>) type.getType());
	}

	@Override
	public <T> Pages<T> getPages(TypeDescriptor resultType, Sql sql,
			long pageNumber, long limit) {
		if (limit <= 0 || pageNumber <= 0) {
			throw new RuntimeException("page=" + pageNumber + ", limit="
					+ limit);
		}

		long start = PageSupport.getStart(pageNumber, limit);
		PaginationSql paginationSql = sqlDialect.toPaginationSql(sql, start,
				limit);
		Long total = query(Long.class, paginationSql.getCountSql()).first();
		if (total == null || total == 0) {
			return PageSupport.emptyPages(pageNumber, limit);
		}

		Cursor<T> cursor = query(resultType, paginationSql.getResultSql());
		Page<T> page = new SharedPage<T>(start, cursor.shared(), limit, total);
		return PageSupport.getPages(
				page,
				(startIndex, count) -> {
					Cursor<T> rows = query(resultType, sqlDialect
							.toPaginationSql(sql, startIndex, count)
							.getResultSql());
					// 因为是分页，每一页的内部没必要使用流，所在这里调用了shared
					return rows.shared().stream();
				});
	}

	public TableChanges getTableChanges(Class<?> tableClass, String tableName) {
		String tName = getTableName(tableName, tableClass, null);
		TableStructureMapping tableStructureMapping = sqlDialect
				.getTableStructureMapping(tableClass, tName);
		List<ColumnDescriptor> list = prepare(tableStructureMapping.getSql())
				.query().process((rs, rowNum) -> {
					return tableStructureMapping.getName(rs);
				});
		HashSet<String> hashSet = new HashSet<String>();
		List<String> deleteList = new ArrayList<String>();
		Fields fields = sqlDialect.getFields(tableClass);
		for (ColumnDescriptor columnDescriptor : list) {
			hashSet.add(columnDescriptor.getName());
			Field column = fields.find(columnDescriptor.getName(), null);
			if (column == null) {// 在现在的表结构中不存在，应该删除
				deleteList.add(columnDescriptor.getName());
			}
		}

		List<Field> addList = new ArrayList<Field>();
		for (Field column : sqlDialect.getFields(tableClass)) {
			String name = sqlDialect.getName(column.getGetter());
			if (!hashSet.contains(name)) {// 在已有的数据库中不存在，应该添加
				addList.add(column);
			}
		}
		return new TableChanges(deleteList, addList);
	}

	@Override
	public <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass,
			String tableName, Field field) {
		String tName = getTableName(tableName, tableClass, null);
		Sql sql = sqlDialect.toMaxIdSql(tableClass, tName, field);
		return query(type, sql).first();
	}

	@Override
	public <T> List<T> getByIdList(String tableName,
			Class<? extends T> entityClass, Object... ids) {
		String tName = getTableName(tableName, entityClass, null);
		Sql sql = sqlDialect.toSelectByIdsSql(tName, entityClass, ids);
		Cursor<T> cursor = query(entityClass, sql);
		return cursor.shared();
	}

	@Override
	public <K, V> Map<K, V> getInIds(String tableName,
			Class<? extends V> entityClass,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		String tName = getTableName(tableName, entityClass, null);
		Sql sql = sqlDialect.getInIds(tName, entityClass, primaryKeys,
				inPrimaryKeys);
		Cursor<V> cursor = query(entityClass, sql);
		List<V> list = cursor.shared();
		if (list == null || list.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, K> keyMap = sqlDialect.getInIdsKeyMap(entityClass,
				inPrimaryKeys, primaryKeys);
		Map<K, V> map = new LinkedHashMap<K, V>();
		for (V v : list) {
			String key = sqlDialect.getObjectKey(entityClass, v);
			map.put(keyMap.get(key), v);
		}
		return map;
	}

	@Override
	public boolean createTable(TableStructure tableStructure) {
		Sql sql = sqlDialect.toCreateTableSql(tableStructure);
		prepare(sql).execute();
		return true;
	}

	@Override
	public <T> int save(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.save(tableStructure, entity);
		return prepare(sql).process(
				(ps) -> {
					int updateCount = ps.executeUpdate();
					setAutoIncrementLastId(updateCount, sql,
							ps.getConnection(), tableStructure.getName(),
							tableStructure.getEntityClass(), entity);
					return updateCount;
				});
	}

	@Override
	public <T> int delete(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.delete(tableStructure, entity);
		return prepare(sql).update();
	}

	@Override
	public <T> int update(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.update(tableStructure, entity);
		return prepare(sql).update();
	}

	@Override
	public <T> int saveOrUpdate(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.toSaveOrUpdateSql(tableStructure, entity);
		return prepare(sql).process(
				(ps) -> {
					int updateCount = ps.executeUpdate();
					setAutoIncrementLastId(updateCount, sql,
							ps.getConnection(), tableStructure.getName(),
							tableStructure.getEntityClass(), entity);
					return updateCount;
				});
	}
}
