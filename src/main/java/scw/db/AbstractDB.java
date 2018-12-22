package scw.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import scw.common.Iterator;
import scw.common.Logger;
import scw.common.utils.ClassUtils;
import scw.database.ConnectionSource;
import scw.database.DataBaseUtils;
import scw.database.SQL;
import scw.database.TableInfo;
import scw.database.TransactionContext;
import scw.database.annoation.Table;
import scw.database.result.Result;
import scw.database.result.ResultSet;
import scw.db.sql.MysqlFormat;
import scw.db.sql.MysqlSelect;
import scw.db.sql.SQLFormat;
import scw.db.sql.Select;

public abstract class AbstractDB implements ConnectionSource, AutoCloseable {
	{
		Logger.info("Init DB for className:" + this.getClass().getName());
	}

	private final SQLFormat sqlFormat;
	
	public AbstractDB(SQLFormat sqlFormat) {
		this.sqlFormat = sqlFormat == null ? new MysqlFormat() : sqlFormat;
	}

	public final SQLFormat getSqlFormat() {
		return sqlFormat;
	}

	public void iterator(Class<?> tableClass, Iterator<Result> iterator) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		iterator(getSqlFormat().toSelectByIdSql(tableInfo, tableInfo.getName()), iterator);
	}

	public void iterator(SQL sql, final Iterator<Result> iterator) {
		DataBaseUtils.iterator(this, sql, new Iterator<java.sql.ResultSet>() {

			public void iterator(java.sql.ResultSet data) {
				try {
					iterator.iterator(new Result(data));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ResultSet select(SQL sql) {
		return TransactionContext.getInstance().select(this, sql);
	}

	public <T> List<T> select(Class<T> type, SQL sql) {
		return select(sql).getList(type);
	}

	public <T> T selectOne(Class<T> type, SQL sql) {
		return select(sql).getObject(type, 0);
	}

	public Select createSelect() {
		return new MysqlSelect(this);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String columnName) {
		Select select = createSelect();
		select.desc(tableClass, columnName);
		return select.getResultSet().getObject(type, 0, tableName);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String columnName) {
		return getMaxValue(type, tableClass, null, columnName);
	}

	public int getMaxIntValue(Class<?> tableClass, String fieldName) {
		Integer maxId = getMaxValue(Integer.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0;
		}
		return maxId;
	}

	public long getMaxLongValue(Class<?> tableClass, String fieldName) {
		Long maxId = getMaxValue(Long.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0L;
		}
		return maxId;
	}

	public void createTable(Class<?> tableClass) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		createTable(tableClass, tableInfo.getName());
	}

	public void createTable(Class<?> tableClass, String tableName) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(tableClass);
		SQL sql = getSqlFormat().toCreateTableSql(tableInfo, tableName);
		Logger.info(sql.getSql());
		DataBaseUtils.execute(this, Arrays.asList(sql));
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

	public <T> T getById(Class<T> type, Object... params) {
		return getById(null, type, params);
	}

	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
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
		SQL sql = getSqlFormat().toSelectByIdSql(tableInfo, tName, params);
		ResultSet resultSet = select(sql);
		return resultSet.getObject(type, 0, tName);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getByIdList(null, type, params);
	}

	public <T> List<T> getByIdList(String tableName, Class<T> type, Object... params) {
		if (type == null) {
			throw new NullPointerException("type is null");
		}

		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
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

	public void execute(Collection<OperationBean> operationBeans) {
		Collection<SQL> sqls = DBUtils.getSqlList(getSqlFormat(), operationBeans);
		if (sqls == null || sqls.isEmpty()) {
			return;
		}

		TransactionContext.getInstance().execute(this, sqls);
	}

	/** 保存 **/
	public void save(Object... beans) {
		save(Arrays.asList(beans));
	}

	public void save(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.SAVE, bean));
		}

		execute(operationBeans);
	}

	/** 删除 **/
	public void delete(Object... beans) {
		delete(Arrays.asList(beans));
	}

	public void delete(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.DELETE, bean));
		}

		execute(operationBeans);
	}

	/** 更新 **/
	public void update(Object... beans) {
		update(Arrays.asList(beans));
	}

	public void update(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.UPDATE, bean));
		}

		execute(operationBeans);
	}

	/** 保存或更新 **/
	public void saveOrUpdate(Object... beans) {
		saveOrUpdate(Arrays.asList(beans));
	}

	public void saveOrUpdate(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.SAVE_OR_UPDATE, bean));
		}

		execute(operationBeans);
	}
}
