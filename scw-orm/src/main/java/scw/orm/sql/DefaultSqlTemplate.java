package scw.orm.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import scw.aop.support.ProxyUtils;
import scw.convert.ConversionService;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.env.Sys;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.mapper.MapperUtils;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.convert.SmartRowMapper;
import scw.sql.ConnectionFactory;
import scw.sql.DefaultSqlOperations;
import scw.sql.ResultSetMapper;
import scw.sql.Sql;
import scw.sql.SqlException;
import scw.sql.orm.enums.OperationType;

public class DefaultSqlTemplate extends DefaultSqlOperations implements SqlTemplate {
	private static Logger logger = LoggerFactory.getLogger(DefaultSqlTemplate.class);
	private final SqlDialect sqlDialect;
	private final ObjectRelationalMapping objectRelationalMapping;
	private ConversionService conversionService;

	public DefaultSqlTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect,
			ObjectRelationalMapping objectRelationalMapping) {
		super(connectionFactory);
		this.sqlDialect = sqlDialect;
		this.objectRelationalMapping = objectRelationalMapping;
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
			entityName = objectRelationalMapping.getName(entityClass);
		}
		return entityName;
	}

	@Override
	public boolean createTable(String tableName, Class<?> entityClass) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.createTable(getTableName(tableName, clazz, null), clazz);
		execute(sql);
		return true;
	}

	private Object getAutoIncrementLastId(Connection connection, String tableName) throws SQLException {
		Sql sql = sqlDialect.toLastInsertIdSql(tableName);
		return query(sql, connection, new ResultSetMapper<Object>() {

			public Object mapper(java.sql.ResultSet resultSet) throws SQLException {
				if (resultSet.next()) {
					return resultSet.getObject(1);
				}
				return null;
			}
		});
	}

	private void setAutoIncrementLastId(int updateCount, Sql sql, Connection connection, String tableName,
			Class<?> entityClass, Object entity) throws SQLException {
		for (Field field : objectRelationalMapping.getSetterFields(entityClass, true, null)) {
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
	public boolean save(String tableName, Object entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		String tName = getTableName(tableName, clazz, entity);
		Sql sql = sqlDialect.save(tName, clazz, entity);
		Connection connection = null;
		try {
			connection = getUserConnection();
			int updateCount = update(sql, connection);
			setAutoIncrementLastId(updateCount, sql, connection, tName, clazz, entity);
			return updateCount > 0;
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		} finally {
			close(connection);
		}
	}
	
	@Override
	public boolean saveOrUpdate(String tableName, Object entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		String tName = getTableName(tableName, clazz, entity);
		Sql sql = sqlDialect.saveOrUpdate(tName, clazz, entity);
		Connection connection = null;
		try {
			connection = getUserConnection();
			int updateCount = update(sql, connection);
			setAutoIncrementLastId(updateCount, sql, connection, tName, clazz, entity);
			return updateCount > 0;
		} catch (SQLException e) {
			throw new SqlException(sql, e);
		} finally {
			close(connection);
		}
	}

	@Override
	public boolean delete(String tableName, Object entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.delete(getTableName(tableName, clazz, entity), clazz, entity);
		return update(sql) > 0;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> entityClass, Object... ids) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.deleteById(getTableName(tableName, clazz, null), clazz, ids);
		return update(sql) > 0;
	}

	@Override
	public boolean update(String tableName, Object entity) {
		Class<?> clazz = getUserEntityClass(entity.getClass());
		Sql sql = sqlDialect.update(getTableName(tableName, clazz, entity), clazz, entity);
		return update(sql) > 0;
	}

	@Override
	public <T> T getById(String tableName, Class<? extends T> entityClass, Object... ids) {
		Class<?> clazz = getUserEntityClass(entityClass);
		Sql sql = sqlDialect.getById(getTableName(tableName, clazz, null), clazz, ids);
		List<T> list = query(entityClass, sql);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public <T> List<T> query(Class<? extends T> entityClass, Sql sql) {
		return query(sql, new SmartRowMapper<T>(entityClass));
	}
}
