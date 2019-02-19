package scw.sql.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import scw.common.utils.StringUtils;
import scw.sql.JdbcTemplate;
import scw.sql.ResultSetMapper;
import scw.sql.Sql;
import scw.sql.orm.result.DefaultResultSet;
import scw.sql.orm.result.ResultSet;

public class JdbcORMTemplate extends JdbcTemplate implements ORMOperations {

	private final SqlFormat sqlFormat;

	public JdbcORMTemplate(SqlFormat sqlFormat) {
		this.sqlFormat = sqlFormat;
	}

	public SqlFormat getSqlFormat() {
		return sqlFormat;
	}

	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
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

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (tableInfo.getPrimaryKeyColumns().length == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (tableInfo.getPrimaryKeyColumns().length != params.length) {
			throw new NullPointerException("params length not equals primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		Sql sql = getSqlFormat().toSelectByIdSql(tableInfo, tName, params);
		ResultSet resultSet = select(sql);
		return resultSet.getFirst().get(type, tName);
	}

	public <T> List<T> getByIdList(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		if (params.length > tableInfo.getPrimaryKeyColumns().length) {
			throw new NullPointerException("params length  greater than primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectByIdSql(tableInfo, tName, params));
		return resultSet.getList(type, tName);
	}

	public ResultSet select(Sql sql) {
		return query(sql, new ResultSetMapper<ResultSet>() {

			public ResultSet mapper(java.sql.ResultSet resultSet) throws SQLException {
				return new DefaultResultSet(resultSet);
			}
		});
	}

	public boolean save(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		Sql sql = sqlFormat.toInsertSql(bean, tableInfo, tName);
		return execute(sql);
	}

	public boolean update(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		Sql sql = sqlFormat.toUpdateSql(bean, tableInfo, tName);
		return execute(sql);
	}

	public boolean delete(Object bean) {
		return delete(bean, null);
	}

	public boolean delete(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		Sql sql = sqlFormat.toDeleteSql(bean, tableInfo, tName);
		return execute(sql);
	}

	public boolean deleteById(Class<?> type, Object... params) {
		return deleteById(null, type, params);
	}

	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		if (type == null) {
			return false;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String tName = StringUtils.isEmpty(tableName) ? tableInfo.getName() : tableName;
		Sql sql = sqlFormat.toDeleteSql(tableInfo, tName, params);
		return execute(sql);
	}

	public boolean saveOrUpdate(Object bean, String tableName) {
		if (bean == null) {
			return false;
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		String tName = ORMUtils.getTableName(tableName, tableInfo, bean);
		Sql sql = sqlFormat.toSaveOrUpdateSql(bean, tableInfo, tName);
		return execute(sql);
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

}
