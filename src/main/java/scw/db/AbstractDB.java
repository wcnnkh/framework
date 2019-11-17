package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import scw.core.Consumer;
import scw.core.Init;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.sql.Sql;
import scw.sql.orm.ORMTemplate;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.annotation.Table;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class AbstractDB extends ORMTemplate implements DB,
		Consumer<AsyncExecute>, DBConfig, Init {

	public void init() {
		if (StringUtils.isNotEmpty(getSannerTablePackage())) {
			createTable(getSannerTablePackage());
		}
		getAsyncQueue().addConsumer(this);
	}

	public void createTable(Class<?> tableClass, boolean registerManager) {
		createTable(tableClass, null, registerManager);
	}

	public void consume(AsyncExecute message) throws Throwable {
		message.execute(this);
	}

	@Override
	public void createTable(Class<?> tableClass, String tableName) {
		createTable(tableClass, tableName, true);
	}

	public void createTable(Class<?> tableClass, String tableName,
			boolean registerManager) {
		if (registerManager) {
			DBManager.register(tableClass, this);
		}
		super.createTable(tableClass, tableName);
	}

	@Override
	public void createTable(String packageName) {
		createTable(packageName, true);
	}

	public void createTable(String packageName, boolean registerManager) {
		Collection<Class<?>> list = ResourceUtils.getClassList(packageName);
		for (Class<?> tableClass : list) {
			Table table = tableClass.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (registerManager) {
				DBManager.register(tableClass, this);
			}

			createTable(tableClass, false);
		}
	}

	@Override
	public SqlFormat getSqlFormat() {
		return getDataBase().getDataBaseType().getSqlFormat();
	}

	@Override
	public Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}

	public void executeSqlByFile(String filePath, boolean lines)
			throws SQLException {
		Collection<Sql> sqls = DBUtils.getSqlByFile(filePath, lines);
		for (Sql sql : sqls) {
			execute(sql);
		}
	}

	@Override
	public boolean save(Object bean, String tableName) {
		boolean b = super.save(bean, tableName);
		if (b) {
			getCacheManager().save(bean);
		}
		return b;
	}

	@Override
	public boolean update(Object bean, String tableName) {
		boolean b = super.update(bean, tableName);
		if (b) {
			getCacheManager().update(bean);
		}
		return b;
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		boolean b = super.delete(bean, tableName);
		if (b) {
			getCacheManager().delete(bean);
		}
		return b;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		boolean b = super.deleteById(tableName, type, params);
		if (b) {
			getCacheManager().deleteById(type, params);
		}
		return b;
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		boolean b = super.saveOrUpdate(bean, tableName);
		if (b) {
			getCacheManager().saveOrUpdate(bean);
		}
		return b;
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		T t = getCacheManager().getById(type, params);
		if (t == null) {
			if (getCacheManager().isExistById(type, params)) {
				t = super.getById(tableName, type, params);
				if (t != null) {
					getCacheManager().save(t);
				}
			}
		}
		return t;
	}

	public void asyncDelete(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.DELETE));
		}
		asyncExecute(asyncExecute);
	}

	public void asyncExecute(Sql... sqls) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Sql sql : sqls) {
			asyncExecute.add(new SqlAsyncExecute(sql));
		}
		asyncExecute(asyncExecute);
	}

	public void asyncSave(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.SAVE));
		}
		asyncExecute(asyncExecute);
	}

	public void asyncUpdate(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.UPDATE));
		}
		asyncExecute(asyncExecute);
	}

	public void asyncExecute(AsyncExecute asyncExecute) {
		getAsyncQueue().push(asyncExecute);
	}
}
