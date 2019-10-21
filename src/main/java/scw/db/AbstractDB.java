package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import scw.core.Constants;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.db.async.AsyncInfo;
import scw.db.async.MultipleOperation;
import scw.db.async.OperationBean;
import scw.db.cache.CacheManager;
import scw.db.database.DataBase;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.orm.ORMTemplate;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.annotation.Table;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class AbstractDB<C extends CacheManager> extends ORMTemplate implements ConnectionFactory, DB {

	public abstract C getCacheManager();

	public abstract void async(AsyncInfo asyncInfo);

	public abstract DataBase getDataBase();

	public void createTable(Class<?> tableClass, boolean registerManager) {
		createTable(tableClass, null, registerManager);
	}

	@Override
	public void createTable(Class<?> tableClass, String tableName) {
		createTable(tableClass, tableName, true);
	}

	public void createTable(Class<?> tableClass, String tableName, boolean registerManager) {
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
	public boolean save(Object bean, String tableName) {
		boolean b = super.save(bean, tableName);
		if (b) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.save(bean);
			}
		}
		return b;
	}

	@Override
	public boolean update(Object bean, String tableName) {
		boolean b = super.update(bean, tableName);
		if (b) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.update(bean);
			}
		}
		return b;
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		boolean b = super.delete(bean, tableName);
		if (b) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.delete(bean);
			}
		}
		return b;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		boolean b = super.deleteById(tableName, type, params);
		if (b) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.deleteById(type, params);
			}
		}
		return b;
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		boolean b = super.saveOrUpdate(bean, tableName);
		if (b) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.saveOrUpdate(bean);
			}
		}
		return b;
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager == null) {
			return super.getById(tableName, type, params);
		}

		T t = cacheManager.getById(type, params);
		if (t == null) {
			if (cacheManager.isExistById(type, params)) {
				t = super.getById(tableName, type, params);
				if (t != null) {
					cacheManager.save(t);
				}
			}
		}
		return t;
	}

	@Override
	public SqlFormat getSqlFormat() {
		return getDataBase().getDataBaseType().getSqlFormat();
	}

	@Override
	public Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}

	public void asyncSave(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.save(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncUpdate(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.update(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncDelete(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.delete(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncSaveOrUpdate(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.saveOrUpdate(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncExecute(OperationBean... operationBeans) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (OperationBean bean : operationBeans) {
			multipleOperation.add(bean);
		}
		asyncExecute(multipleOperation);
	}

	public final void asyncExecute(MultipleOperation multipleOperation) {
		if (TransactionManager.hasTransaction()) {
			AsyncInfoTransactionLifeCycle aitlc = new AsyncInfoTransactionLifeCycle((new AsyncInfo(multipleOperation)));
			TransactionManager.transactionLifeCycle(aitlc);
		} else {
			async(new AsyncInfo(multipleOperation));
		}
	}

	/**
	 * 异步执行sql语句
	 * 
	 * @param sql
	 */
	public final void asyncExecute(Sql... sql) {
		if (TransactionManager.hasTransaction()) {
			AsyncInfoTransactionLifeCycle aitlc = new AsyncInfoTransactionLifeCycle(
					(new AsyncInfo(Arrays.asList(sql))));
			TransactionManager.transactionLifeCycle(aitlc);
		} else {
			async(new AsyncInfo(Arrays.asList(sql)));
		}
	}

	private final class AsyncInfoTransactionLifeCycle extends DefaultTransactionLifeCycle {
		private final AsyncInfo asyncInfo;

		public AsyncInfoTransactionLifeCycle(AsyncInfo asyncInfo) {
			this.asyncInfo = asyncInfo;
		}

		@Override
		public void afterProcess() {
			async(asyncInfo);
			super.afterProcess();
		}
	}

	public void executeSqlByFile(String filePath) {
		String sql = ResourceUtils.getFileContent(filePath, Constants.DEFAULT_CHARSET_NAME);
		if (StringUtils.isEmpty(sql)) {
			return;
		}

		execute(new SimpleSql(sql));
	}

	public void executeSqlByFileLine(String filePath, String ignoreStartsWith) throws SQLException {
		Collection<String> sqlList = ResourceUtils.getFileContentLineList(filePath, Constants.DEFAULT_CHARSET_NAME);
		for (String sql : sqlList) {
			if (!StringUtils.isEmpty(ignoreStartsWith) && sql.startsWith(ignoreStartsWith)) {
				continue;
			}

			execute(new SimpleSql(sql));
		}
	}

	public void executeSqlsByFileLine(String filePath) throws SQLException {
		executeSqlByFileLine(filePath, "##");
	}
}
