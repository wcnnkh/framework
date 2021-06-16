package scw.orm.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import scw.aop.support.ProxyUtils;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.StringUtils;
import scw.env.Sys;
import scw.json.JSONUtils;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.orm.cache.CacheManager;
import scw.orm.generator.DefaultGeneratorProcessor;
import scw.orm.generator.GeneratorProcessor;
import scw.orm.sql.convert.SmartRowMapper;
import scw.sql.ConnectionFactory;
import scw.sql.DefaultSqlOperations;
import scw.sql.RowMapper;
import scw.sql.Sql;
import scw.sql.SqlProcessor;
import scw.util.Pagination;

public class DefaultSqlTemplate extends DefaultSqlOperations implements SqlTemplate {
	private static Logger logger = LoggerFactory.getLogger(DefaultSqlTemplate.class);
	private final SqlDialect sqlDialect;
	private ConversionService conversionService;
	private CacheManager cacheManager;
	private GeneratorProcessor generatorProcessor;

	public DefaultSqlTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory);
		this.sqlDialect = sqlDialect;
		this.generatorProcessor = new DefaultGeneratorProcessor(this);
	}

	public GeneratorProcessor getGeneratorProcessor() {
		return generatorProcessor;
	}

	public void setGeneratorProcessor(GeneratorProcessor generatorProcessor) {
		this.generatorProcessor = generatorProcessor;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
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

	@Override
	public boolean createTable(String tableName, Class<?> entityClass) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.toCreateTableSql(getTableName(tableName, clazz, null), clazz);
		execute(sql);
		return true;
	}

	private Object getAutoIncrementLastId(Connection connection, String tableName) throws SQLException {
		Sql sql = sqlDialect.toLastInsertIdSql(tableName);
		return query(connection, sql, new SqlProcessor<ResultSet, Object>() {

			public Object process(ResultSet resultSet) throws SQLException {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}
		});
	}

	private void setAutoIncrementLastId(int updateCount, Sql sql, Connection connection, String tableName,
			Class<?> entityClass, Object entity) throws SQLException {
		for (Field field : sqlDialect.getFields(entityClass)) {
			if (sqlDialect.isAutoIncrement(field.getSetter())) {
				if (updateCount == 0) {
					logger.error("Number of rows affected is 0, execute: {}", sql);
				} else if (updateCount == 1) {
					Object lastId = getAutoIncrementLastId(connection, tableName);
					field.getSetter().set(entity, lastId, getConversionService());
				}
			}
		}
	}

	@Override
	public <T> boolean save(String tableName, Class<? extends T> entityClass, T entity) {
		generatorProcessor.process(entityClass, entity);
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.save(entityClass, entity)) {
				logger.error("save [{}] to cache error: {}", entityClass,
						JSONUtils.getJsonSupport().toJSONString(entity));
				return false;
			}
		}

		String tName = getTableName(tableName, entityClass, entity);
		Sql sql = sqlDialect.save(tName, entityClass, entity);
		return process(sql, new SqlProcessor<PreparedStatement, Integer>() {

			@Override
			public Integer process(PreparedStatement statement) throws SQLException {
				int updateCount = statement.executeUpdate();
				setAutoIncrementLastId(updateCount, sql, statement.getConnection(), tName, entityClass, entity);
				return updateCount;
			}
		}) > 0;
	}

	@Override
	public <T> boolean saveOrUpdate(String tableName, Class<? extends T> entityClass, T entity) {
		generatorProcessor.process(entityClass, entity);
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.saveOrUpdate(entityClass, entity)) {
				logger.error("saveOrUpdate [{}] to cache error: {}", entityClass,
						JSONUtils.getJsonSupport().toJSONString(entity));
				return false;
			}
		}

		String tName = getTableName(tableName, entityClass, entity);
		Sql sql = sqlDialect.toSaveOrUpdateSql(tName, entityClass, entity);
		return process(sql, new SqlProcessor<PreparedStatement, Integer>() {

			@Override
			public Integer process(PreparedStatement statement) throws SQLException {
				int updateCount = statement.executeUpdate();
				setAutoIncrementLastId(updateCount, sql, statement.getConnection(), tName, entityClass, entity);
				return updateCount;
			}
		}) > 0;
	}

	@Override
	public <T> boolean delete(String tableName, Class<? extends T> entityClass, T entity) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.delete(entity)) {
				logger.error("delete [{}] to cache error: {}", entityClass,
						JSONUtils.getJsonSupport().toJSONString(entity));
				return false;
			}
		}

		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.delete(getTableName(tableName, clazz, entity), clazz, entity);
		return update(sql) > 0;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> entityClass, Object... ids) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.deleteById(entityClass, ids)) {
				logger.error("deleteById [{}] to cache error: {}", entityClass, Arrays.toString(ids));
				return false;
			}
		}

		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.deleteById(getTableName(tableName, clazz, null), clazz, ids);
		return update(sql) > 0;
	}

	@Override
	public <T> boolean update(String tableName, Class<? extends T> entityClass, T entity) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.update(entityClass, entity)) {
				logger.error("update [{}] to cache error: {}", entityClass, entity);
				return false;
			}
		}

		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.update(getTableName(tableName, clazz, entity), clazz, entity);
		return update(sql) > 0;
	}

	@Override
	public <T> T getById(String tableName, Class<? extends T> entityClass, Object... ids) {
		CacheManager cacheManager = getCacheManager();
		T value = null;
		if (cacheManager != null) {
			value = cacheManager.getById(entityClass, ids);
		}

		if (cacheManager == null || (value == null && cacheManager.isKeepLooking(entityClass, ids))) {
			Class<?> clazz = getUserEntityClass(entityClass);
			Sql sql = sqlDialect.toSelectByIdsSql(getTableName(tableName, clazz, null), clazz, ids);
			value = queryFirst(entityClass, sql);
			if (value != null && cacheManager != null) {
				cacheManager.save(value);
			}
		}
		return value;
	}

	@Override
	public <T> Stream<T> streamQuery(Connection connection, TypeDescriptor resultTypeDescriptor, Sql sql) {
		return streamQuery(connection, sql, new SmartRowMapper<T>(sqlDialect, resultTypeDescriptor));
	}

	@Override
	public <T> Pagination<T> paginationQuery(TypeDescriptor resultType, Sql sql, long page, int limit) {
		if (limit <= 0 || page <= 0) {
			throw new RuntimeException("page=" + page + ", limit=" + limit);
		}

		long start = Pagination.getBegin(page, limit);
		PaginationSql paginationSql = sqlDialect.toPaginationSql(sql, start, limit);
		Long count = streamQuery(Long.class, sql).findFirst().orElse(0L);
		Pagination<T> pagination = new Pagination<T>(limit);
		if (count == null || count == 0) {
			pagination.emptyData();
			return pagination;
		} else {
			pagination.setTotalCount(count);
			pagination.setData(query(resultType, paginationSql.getResultSql()));
		}
		return pagination;
	}

	public TableChanges getTableChanges(Class<?> tableClass, String tableName) {
		String tName = getTableName(tableName, tableClass, null);
		TableStructureMapping tableStructureMapping = sqlDialect.getTableStructureMapping(tableClass, tName);
		List<String> list = query(tableStructureMapping.getSql(), new RowMapper<String>() {

			public String mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
				return tableStructureMapping.getName(rs);
			}
		});
		HashSet<String> hashSet = new HashSet<String>();
		List<String> deleteList = new ArrayList<String>();
		Fields fields = sqlDialect.getFields(tableClass);
		for (String name : list) {
			hashSet.add(name);
			Field column = fields.find(name, null);
			if (column == null) {// 在现在的表结构中不存在，应该删除
				deleteList.add(name);
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
		return queryFirst(type, sql);
	}

	@Override
	public <T> List<T> getByIdList(String tableName, Class<? extends T> entityClass, Object... ids) {
		String tName = getTableName(tableName, entityClass, null);
		Sql sql = sqlDialect.toSelectByIdsSql(tName, entityClass, ids);
		return query(entityClass, sql);
	}

	@Override
	public <K, V> Map<K, V> getInIds(String tableName, Class<? extends V> entityClass,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		String tName = getTableName(tableName, entityClass, null);
		Sql sql = sqlDialect.getInIds(tName, entityClass, primaryKeys, inPrimaryKeys);
		List<V> list = query(entityClass, sql);
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
}
