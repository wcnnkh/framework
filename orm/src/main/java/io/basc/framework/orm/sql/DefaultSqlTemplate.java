package io.basc.framework.orm.sql;

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

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapProcessDecorator;
import io.basc.framework.orm.sql.convert.SmartMapProcessor;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.DefaultSqlOperations;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlException;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.page.Page;
import io.basc.framework.util.page.PageSupport;
import io.basc.framework.util.page.Pages;
import io.basc.framework.util.page.SharedPage;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.Processor;

public class DefaultSqlTemplate extends DefaultSqlOperations implements SqlTemplate {
	private static Logger logger = LoggerFactory.getLogger(DefaultSqlTemplate.class);
	private final SqlDialect sqlDialect;
	private ConversionService conversionService;

	public DefaultSqlTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory);
		this.sqlDialect = sqlDialect;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	protected Class<?> getUserEntityClass(Class<?> entityClass) {
		return ProxyUtils.getFactory().getUserClass(entityClass);
	}

	protected <T> String getTableName(@Nullable String tableName, Class<? extends T> entityClass, @Nullable T entity) {
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

	private Object getAutoIncrementLastId(Connection connection, String tableName) throws SQLException {
		Sql sql = sqlDialect.toLastInsertIdSql(tableName);
		return query(connection, sql, (rs) -> rs.getObject(1)).first();
	}

	private void setAutoIncrementLastId(int updateCount, Sql sql, Connection connection, String tableName,
			Class<?> entityClass, Object entity) throws SQLException {
		for (Column column : sqlDialect.resolve(entityClass)) {
			if (column.isAutoIncrement() && column.getField() != null) {
				if (updateCount == 0) {
					logger.error("Number of rows affected is 0, execute: {}", sql);
				} else if (updateCount == 1) {
					Object lastId = getAutoIncrementLastId(connection, tableName);
					column.getField().getSetter().set(entity, lastId, getConversionService());
				}
			}
		}
	}

	@Override
	public <T> int save(String tableName, Class<? extends T> entityClass, T entity) {
		String tName = getTableName(tableName, entityClass, entity);
		Sql sql = sqlDialect.save(tName, entityClass, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(updateCount, sql, ps.getConnection(), tName, entityClass, entity);
			return updateCount;
		});
	}

	@Override
	public <T> int saveOrUpdate(String tableName, Class<? extends T> entityClass, T entity) {
		String tName = getTableName(tableName, entityClass, entity);
		Sql sql = sqlDialect.toSaveOrUpdateSql(tName, entityClass, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(updateCount, sql, ps.getConnection(), tName, entityClass, entity);
			return updateCount;
		});
	}

	@Override
	public <T> int delete(String tableName, Class<? extends T> entityClass, T entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.delete(getTableName(tableName, clazz, entity), clazz, entity, null);
		return prepare(sql).update();
	}

	@Override
	public boolean deleteById(String tableName, Class<?> entityClass, Object... ids) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.deleteById(getTableName(tableName, clazz, null), clazz, ids);
		return prepare(sql).update() > 0;
	}

	@Override
	public <T> int update(String tableName, Class<? extends T> entityClass, T entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.update(getTableName(tableName, clazz, entity), clazz, entity, null);
		return prepare(sql).update();
	}

	@Override
	public <T> T getById(String tableName, Class<? extends T> entityClass, Object... ids) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.toSelectByIdsSql(getTableName(tableName, clazz, null), clazz, ids);
		return query(entityClass, sql).first();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Processor<ResultSet, T, Throwable> getMapProcessor(TypeDescriptor type) {
		return new MapProcessDecorator<>(getMapper(), new SmartMapProcessor<>(sqlDialect, type),
				(Class<T>) type.getType());
	}

	@Override
	public <T> Pages<T> getPages(Sql sql, long pageNumber, long limit,
			Processor<ResultSet, T, ? extends Throwable> mapProcessor) {
		if (limit <= 0 || pageNumber <= 0) {
			throw new RuntimeException("page=" + pageNumber + ", limit=" + limit);
		}

		long start = PageSupport.getStart(pageNumber, limit);
		PaginationSql paginationSql = sqlDialect.toPaginationSql(sql, start, limit);
		Long total = query(Long.class, paginationSql.getCountSql()).first();
		if (total == null || total == 0) {
			return PageSupport.emptyPages(pageNumber, limit);
		}

		Cursor<T> cursor = query(paginationSql.getResultSql(), mapProcessor);
		Page<T> page = new SharedPage<T>(start, cursor.shared(), limit, total);
		return PageSupport.getPages(page, (startIndex, count) -> {
			Cursor<T> rows = query(sqlDialect.toPaginationSql(sql, startIndex, count).getResultSql(), mapProcessor);
			// 因为是分页，每一页的内部没必要使用流，所在这里调用了shared
			return rows.shared().stream();
		});
	}

	public TableChanges getTableChanges(Class<?> tableClass, String tableName) {
		String tName = getTableName(tableName, tableClass, null);
		TableStructureMapping tableStructureMapping = sqlDialect.getTableStructureMapping(tableClass, tName);
		List<ColumnDescriptor> list = prepare(tableStructureMapping.getSql()).query().process((rs, rowNum) -> {
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
	public <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass, String tableName, Field field) {
		String tName = getTableName(tableName, tableClass, null);
		Sql sql = sqlDialect.toMaxIdSql(tableClass, tName, field);
		return query(type, sql).first();
	}

	@Override
	public <T> List<T> getByIdList(String tableName, Class<? extends T> entityClass, Object... ids) {
		String tName = getTableName(tableName, entityClass, null);
		Sql sql = sqlDialect.toSelectByIdsSql(tName, entityClass, ids);
		Cursor<T> cursor = query(entityClass, sql);
		return cursor.shared();
	}

	@Override
	public <K, V> Map<K, V> getInIds(String tableName, Class<? extends V> entityClass,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		String tName = getTableName(tableName, entityClass, null);
		Sql sql = sqlDialect.getInIds(tName, entityClass, primaryKeys, inPrimaryKeys);
		Cursor<V> cursor = query(entityClass, sql);
		List<V> list = cursor.shared();
		if (list == null || list.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, K> keyMap = sqlDialect.getInIdsKeyMap(entityClass, inPrimaryKeys, primaryKeys);
		Map<K, V> map = new LinkedHashMap<K, V>();
		for (V v : list) {
			String key = sqlDialect.getObjectKey(entityClass, v);
			map.put(keyMap.get(key), v);
		}
		return map;
	}

	@Override
	public boolean createTable(TableStructure tableStructure) {
		Collection<Sql> sqls = sqlDialect.createTable(tableStructure);
		try {
			process((conn) -> {
				for (Sql sql : sqls) {
					prepare(conn, sql).execute();
				}
			});
		} catch (SQLException e) {
			throw new SqlException(tableStructure.getEntityClass().getName(), e);
		}
		return true;
	}

	@Override
	public <T> int save(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.save(tableStructure, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(updateCount, sql, ps.getConnection(), tableStructure.getName(),
					tableStructure.getEntityClass(), entity);
			return updateCount;
		});
	}

	@Override
	public <T> int delete(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.delete(tableStructure, entity, null);
		return prepare(sql).update();
	}

	@Override
	public <T> int update(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.update(tableStructure, entity, null);
		return prepare(sql).update();
	}

	@Override
	public <T> int saveOrUpdate(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.toSaveOrUpdateSql(tableStructure, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(updateCount, sql, ps.getConnection(), tableStructure.getName(),
					tableStructure.getEntityClass(), entity);
			return updateCount;
		});
	}

	@Override
	public <T> boolean saveIfAbsent(String tableName, Class<? extends T> entityClass, T entity) {
		String tName = getTableName(tableName, entityClass, entity);
		Sql sql = sqlDialect.toSaveIfAbsentSql(tName, entityClass, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(updateCount, sql, ps.getConnection(), tName, entityClass, entity);
			return updateCount;
		}) > 0;
	}

	@Override
	public <T> boolean saveIfAbsent(TableStructure tableStructure, T entity) {
		Sql sql = sqlDialect.toSaveIfAbsentSql(tableStructure, entity);
		return prepare(sql).process((ps) -> {
			int updateCount = ps.executeUpdate();
			setAutoIncrementLastId(updateCount, sql, ps.getConnection(), tableStructure.getName(),
					tableStructure.getEntityClass(), entity);
			return updateCount;
		}) > 0;
	}
}
