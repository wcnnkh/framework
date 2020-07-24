package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.aop.ProxyUtils;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanFactoryAware;
import scw.beans.Destroy;
import scw.core.Constants;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.orm.Column;
import scw.sql.orm.TableChange;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.cache.CacheManager;
import scw.sql.orm.cache.DefaultCacheManager;
import scw.sql.orm.cache.TemporaryCacheManager;
import scw.sql.orm.enums.OperationType;
import scw.sql.orm.support.AbstractEntityOperations;
import scw.sql.orm.support.generation.DefaultGeneratorService;
import scw.sql.orm.support.generation.GeneratorService;
import scw.sql.transaction.SqlTransactionUtils;
import scw.util.ClassScanner;
import scw.util.Consumer;
import scw.util.queue.MemoryAsyncExecuteQueue;

@SuppressWarnings("rawtypes")
public abstract class AbstractDB extends AbstractEntityOperations
		implements DB, Consumer<AsyncExecute>, BeanFactoryAware, Destroy, ConnectionFactory {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	private final MemoryAsyncExecuteQueue<AsyncExecute> asyncExecuteQueue = new MemoryAsyncExecuteQueue<AsyncExecute>(getClass().getName(), true);
	private BeanFactory beanFactory;
	private CacheManager cacheManager;
	private GeneratorService generatorService;
	
	{
		asyncExecuteQueue.addConsumer(this);
	}

	public AbstractDB(Map properties) {
		this.cacheManager = new DefaultCacheManager();
		this.generatorService = new DefaultGeneratorService();
	}

	public AbstractDB(Map properties, Memcached memcached) {
		this.cacheManager = new TemporaryCacheManager(memcached, true, getCachePrefix(properties));
		this.generatorService = new MemcachedGeneratorService(memcached);
	}

	public AbstractDB(Map properties, Redis redis) {
		this.cacheManager = new TemporaryCacheManager(redis, true, getCachePrefix(properties));
		this.generatorService = new RedisGeneratorService(redis);
	}

	public AbstractDB(CacheManager cacheManager, GeneratorService generatorService) {
		this.cacheManager = cacheManager;
		this.generatorService = generatorService;
	}
	
	public void accept(AsyncExecute message) {
		processing(message, false);
	}

	protected String getCachePrefix(Map properties) {
		if(properties == null){
			return null;
		}
		return StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
	}

	protected void createTableByProperties(Map properties) {
		if(properties == null){
			return ;
		}
		
		String create = StringUtils.toString(properties.get("create"), null);
		if (StringUtils.isNotEmpty(create)) {
			createTable(create);
		}
	}

	@Override
	public GeneratorService getGeneratorService() {
		return generatorService;
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void createTable(Class<?> tableClass, boolean registerManager) {
		createTable(tableClass, null, registerManager);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void processing(AsyncExecute asyncExecute, boolean isLogger) {
		if (asyncExecute == null) {
			return;
		}

		if (beanFactory != null) {
			Class<?> clazz = ProxyUtils.getProxyFactory().getUserClass(asyncExecute.getClass());
			BeanDefinition definition = beanFactory.getDefinition(clazz);
			if (definition != null) {
				try {
					definition.dependence(asyncExecute);
					definition.init(asyncExecute);
				} catch (Exception e) {
					logger.error(e, "dependence {} error", clazz);
				}
			}
		}

		if (isLogger) {
			logger.info("async processing: {}", asyncExecute);
		}

		try {
			asyncExecute.execute(this);
		} catch (Exception e) {
			logger.error(e, "async processing error");
		}
	}

	public synchronized void destroy() {
		asyncExecuteQueue.destroy();
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
			for (Column column : tableChange.getAddColumnss()) {
				addList.add(column.getName());
			}
		}

		if (!CollectionUtils.isEmpty(tableChange.getDeleteColumns()) || !CollectionUtils.isEmpty(addList)) {
			// 如果存在字段变更
			if (logger.isWarnEnabled()) {
				logger.warn("There are field changes class={}, addList={}, deleteList={}", tableClass.getName(),
						Arrays.toString(addList.toArray()), Arrays.toString(tableChange.getDeleteColumns().toArray()));
			}
		}
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

	public final void asyncSaveOrUpdate(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.SAVE_OR_UPDATE));
		}
		asyncExecute(asyncExecute);
	}

	public void asyncExecute(AsyncExecute asyncExecute) {
		if(!asyncExecuteQueue.isStarted()){
			throw new RuntimeException("Asynchronous processing has stopped!");
		}
		
		asyncExecuteQueue.put(asyncExecute);
	}

	@Override
	public Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}

	@Deprecated
	public Select createSelect() {
		return new MysqlSelect(this);
	}
}
