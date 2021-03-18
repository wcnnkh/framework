package scw.db;

import java.util.concurrent.atomic.AtomicBoolean;

import scw.beans.BeanFactory;
import scw.core.utils.StringUtils;
import scw.data.TemporaryStorage;
import scw.db.database.DataBase;
import scw.env.PropertyManager;
import scw.env.SystemEnvironment;
import scw.env.support.DefaultPropertyManager;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.sql.orm.cache.CacheManager;
import scw.sql.orm.cache.TemporaryCacheManager;
import scw.sql.orm.dialect.SqlDialect;

public abstract class ConfigurableDB extends AbstractDB {
	private static Logger logger = LoggerFactory
			.getLogger(ConfigurableDB.class);
	private static final String DEFAULT_CACHE_PREFIX = "db:";
	private final AtomicBoolean init = new AtomicBoolean(false);
	private volatile PropertyManager propertyManager;
	private DataBase dataBase;
	private TemporaryStorage temporaryCache;

	public ConfigurableDB() {
	}

	public ConfigurableDB(String configLocation) {
		loadProperties(configLocation);
	}

	public void setTemporaryCache(TemporaryStorage temporaryCache) {
		this.temporaryCache = temporaryCache;
	}

	public void loadProperties(String configLocation) {
		if (propertyManager == null) {
			synchronized (this) {
				if (propertyManager == null) {
					this.propertyManager = new DefaultPropertyManager(false);
				}
			}
		}
		
		propertyManager.loadProperties(SystemEnvironment.getInstance().getProperties(configLocation)).register();
	}

	/**
	 * 可能为空
	 * 
	 * @return
	 */
	@Nullable
	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	public void setDataBase(DataBase dataBase) {
		this.dataBase = dataBase;
	}

	@Override
	public SqlDialect getSqlDialect() {
		DataBase dataBase = getDataBase();
		if (dataBase != null) {
			return dataBase.getSqlDialect();
		}
		return super.getSqlDialect();
	}

	private TemporaryStorage getTemporaryCache() {
		if (temporaryCache != null) {
			return temporaryCache;
		}

		BeanFactory beanFactory = getBeanFactory();
		if (beanFactory != null) {
			if (beanFactory.isInstance(TemporaryStorage.class)) {
				return beanFactory.getInstance(TemporaryStorage.class);
			}
		}
		return null;
	}

	@Override
	protected CacheManager createDefaultCacheManager() {
		TemporaryStorage temporaryCache = getTemporaryCache();
		if (temporaryCache != null) {
			String keyPrefix = getCachePrefix();
			logger.info("Use temporary cache [{}], key prefix [{}]",
					temporaryCache, keyPrefix);
			return new TemporaryCacheManager(temporaryCache, true, keyPrefix);
		}
		return super.createDefaultCacheManager();
	}

	protected String getCachePrefix() {
		if (propertyManager == null) {
			return DEFAULT_CACHE_PREFIX;
		}
		return StringUtils.toString(propertyManager.getString("cache.prefix"), "")
				+ DEFAULT_CACHE_PREFIX;
	}

	public boolean isInitialized() {
		return init.get();
	}

	public final boolean initializing() {
		if (init.get()) {
			return false;
		}

		if (init.compareAndSet(false, true)) {
			initConfig();
			return true;
		}

		return false;
	}

	protected void initConfig() {
		if (dataBase != null) {
			boolean create = true;
			if (propertyManager != null) {
				create = propertyManager.getValue("create.database", boolean.class,
						create);
			}

			if (create) {
				dataBase.create();
			}
		}

		if (propertyManager == null) {
			return;
		}
		setCheckTableChange(propertyManager.getValue("check.table.change",
				boolean.class, true));
		String create = StringUtils.toString(propertyManager.getString("create"),
				null);
		if (StringUtils.isNotEmpty(create)) {
			createTable(create, propertyManager.getValue("table.register.manager",
					boolean.class, true));
		}
	}
}
