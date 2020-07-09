package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scw.aop.ProxyUtils;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.Init;
import scw.beans.annotation.Autowired;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.serialzer.SerializerUtils;
import scw.sql.Sql;
import scw.sql.orm.Column;
import scw.sql.orm.TableChange;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.dialect.SqlDialect;
import scw.sql.orm.enums.OperationType;
import scw.sql.orm.support.AbstractEntityOperations;
import scw.transaction.sql.SqlTransactionUtils;
import scw.util.ClassScanner;
import scw.util.queue.Consumer;

public abstract class AbstractDB extends AbstractEntityOperations implements DB,
		Consumer<AsyncExecute>, DBConfig, Init {
	@Autowired
	private BeanFactory beanFactory;

	public SqlDialect getSqlDialect() {
		return getDataBase().getSqlDialect();
	}

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
		if(beanFactory != null){
			Class<?> clazz = ProxyUtils.getProxyFactory().getUserClass(message.getClass());
			BeanDefinition beanDefinition = beanFactory.getDefinition(clazz.getName());
			if(beanDefinition != null){
				beanDefinition.dependence(message);
				beanDefinition.init(message);
			}
		}
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
		// 检查表变更
		checkTableChange(tableClass);
	}

	@Override
	public void createTable(String packageName) {
		createTable(packageName, true);
	}

	public void createTable(String packageName, boolean registerManager) {
		Collection<Class<?>> list = ClassScanner.getInstance().getClasses(packageName);
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

	// 检查表变更
	protected void checkTableChange(Class<?> tableClass) {
		TableChange tableChange = getTableChange(tableClass);
		List<String> addList = new LinkedList<String>();
		if (!CollectionUtils.isEmpty(tableChange.getAddColumnss())) {
			for (Column column : tableChange
					.getAddColumnss()) {
				addList.add(column.getName());
			}
		}
		
		if (!CollectionUtils.isEmpty(tableChange.getDeleteColumns())
				|| !CollectionUtils.isEmpty(addList)) {
			// 如果存在字段变更
			if (logger.isWarnEnabled()) {
				logger.warn("There are field changes class={}, addList={}, deleteList={}",
						tableClass.getName(),
						Arrays.toString(addList.toArray()),
						Arrays.toString(tableChange.getDeleteColumns().toArray()));
			}
		}
	}

	@Override
	public Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}

	public final void asyncDelete(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.DELETE));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncExecute(Sql... sqls) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Sql sql : sqls) {
			asyncExecute.add(new SqlAsyncExecute(sql));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncSave(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.SAVE));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncUpdate(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.UPDATE));
		}
		asyncExecute(asyncExecute);
	}
	
	public void asyncSaveOrUpdate(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.SAVE_OR_UPDATE));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncExecute(AsyncExecute asyncExecute) {
		getAsyncQueue().push(SerializerUtils.clone(asyncExecute));
	}
	
	@Deprecated
	public Select createSelect(){
		return new MysqlSelect(this);
	}
}
