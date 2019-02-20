package scw.sql.orm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.common.Logger;
import scw.common.Pagination;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.sql.AbstractSqlTemplate;
import scw.sql.Sql;
import scw.sql.orm.annoation.Table;
import scw.sql.orm.plugin.SelectCacheUtils;
import scw.sql.orm.result.ResultSet;

public abstract class AbstractORMTemplate extends AbstractSqlTemplate implements ORMOperations {

	private final SqlFormat sqlFormat;

	public AbstractORMTemplate(SqlFormat sqlFormat) {
		this.sqlFormat = sqlFormat;
	}

	public final SqlFormat getSqlFormat() {
		return sqlFormat;
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

	public void createTable(Class<?> tableClass) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		createTable(tableClass, tableInfo.getName());
	}

	public void createTable(Class<?> tableClass, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		Sql sql = getSqlFormat().toCreateTableSql(tableInfo, tableName);
		Logger.info(this.getClass().getName(), sql.getSql());
		execute(sql);
	}

	public void createTable(String packageName) {
		Collection<Class<?>> list = ClassUtils.getClasses(packageName);
		for (Class<?> tableClass : list) {
			Table table = tableClass.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (table.create()) {
				createTable(tableClass);
			}
		}
	}

	public ResultSet select(Sql sql) {
		return SelectCacheUtils.select(this, sql);
	}

	public <T> List<T> select(Class<T> type, Sql sql) {
		return select(sql).getList(type);
	}

	public <T> T selectOne(Class<T> type, Sql sql) {
		return select(sql).getFirst().get(type);
	}

	@SuppressWarnings("unchecked")
	public <T> Pagination<List<T>> select(Class<T> type, long page, int limit, Sql sql) {
		PaginationSql paginationSql = sqlFormat.toPaginationSql(sql, page, limit);
		Long count = selectOne(Long.class, paginationSql.getCountSql());
		if (count == null) {
			count = 0L;
		}

		if (count == 0) {
			return new Pagination<List<T>>(0, limit, Collections.EMPTY_LIST);
		}

		return new Pagination<List<T>>(count, limit, select(type, paginationSql.getResultSql()));
	}

	public Pagination<ResultSet> select(long page, int limit, Sql sql) {
		PaginationSql paginationSql = sqlFormat.toPaginationSql(sql, page, limit);
		Long count = selectOne(Long.class, paginationSql.getCountSql());
		if (count == null) {
			count = 0L;
		}

		if (count == 0) {
			return new Pagination<ResultSet>(0, limit, ResultSet.EMPTY_RESULTSET);
		}

		return new Pagination<ResultSet>(count, limit, select(paginationSql.getResultSql()));
	}

	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (inIds == null || inIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		if (tableInfo == null) {
			throw new NullPointerException("tableInfo is null");
		}

		ColumnInfo columnInfo = tableInfo.getPrimaryKeyColumns()[params.length];
		if (params.length > tableInfo.getPrimaryKeyColumns().length - 1) {
			throw new NullPointerException("params length  greater than primary key lenght");
		}

		String tName = (tableName == null || tableName.length() == 0) ? tableInfo.getName() : tableName;
		ResultSet resultSet = select(getSqlFormat().toSelectInIdSql(tableInfo, tName, params, inIds));
		List<V> list = resultSet.getList(type, tName);
		if (list == null || list.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<K, V> map = new HashMap<K, V>();
		for (V v : list) {
			K k;
			try {
				k = (K) columnInfo.getFieldInfo().forceGet(v);
				if (map.containsKey(k)) {
					throw new AlreadyExistsException(k + "");
				}
				map.put(k, v);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	public <K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIdList, Object... params) {
		return getInIdList(type, null, inIdList, params);
	}
}
