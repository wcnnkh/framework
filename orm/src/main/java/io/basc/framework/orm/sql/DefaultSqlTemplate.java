package io.basc.framework.orm.sql;

import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.cache.CacheManager;
import io.basc.framework.orm.generator.DefaultGeneratorProcessor;
import io.basc.framework.orm.generator.GeneratorProcessor;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.DefaultSqlOperations;
import io.basc.framework.util.Assert;

public class DefaultSqlTemplate extends DefaultSqlOperations implements SqlTemplate {
	private final SqlDialect sqlDialect;
	private GeneratorProcessor generatorProcessor;
	private CacheManager cacheManager;

	public DefaultSqlTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory);
		this.generatorProcessor = new DefaultGeneratorProcessor(sqlDialect, this);
		this.sqlDialect = sqlDialect;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	@Nullable
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public GeneratorProcessor getGeneratorProcessor() {
		return generatorProcessor;
	}

	public void setGeneratorProcessor(GeneratorProcessor generatorProcessor) {
		Assert.requiredArgument(generatorProcessor != null, "generatorProcessor");
		this.generatorProcessor = generatorProcessor;
	}

	@Override
	public <T> void save(Class<? extends T> entityClass, T entity) {
		generatorProcessor.process(entityClass, entity);
		// 为什么先执行，因为可能是数据库自增，这样可以将自增值也保存在缓存中
		SqlTemplate.super.save(entityClass, entity);
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			cacheManager.save(entityClass, entity);
		}
	}

	@Override
	public <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		generatorProcessor.process(entityClass, entity);
		if (SqlTemplate.super.saveIfAbsent(entityClass, entity)) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.saveIfAbsent(entityClass, entity);
			}
			return true;
		}
		return false;
	}

	@Override
	public <T> boolean delete(Class<? extends T> entityClass, T entity) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			cacheManager.delete(entity);
		}
		return SqlTemplate.super.delete(entityClass, entity);
	}

	@Override
	public boolean deleteById(Class<?> entityClass, Object... ids) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			cacheManager.deleteById(entityClass, ids);
		}
		return SqlTemplate.super.deleteById(entityClass, ids);
	}

	@Override
	public <T> boolean update(Class<? extends T> entityClass, T entity) {
		if (SqlTemplate.super.update(entityClass, entity)) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.update(entityClass, entity);
			}
			return true;
		}
		return false;
	}

	@Override
	public <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		generatorProcessor.process(entityClass, entity);
		if (SqlTemplate.super.saveOrUpdate(entityClass, entity)) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.saveOrUpdate(entity);
			}
			return true;
		}
		return false;
	}

	@Override
	public <T> T getById(Class<? extends T> entityClass, Object... ids) {
		CacheManager cacheManager = getCacheManager();
		T value = null;
		if (cacheManager != null) {
			value = cacheManager.getById(entityClass, ids);
		}

		if (cacheManager == null || (value == null && cacheManager.isKeepLooking(entityClass, ids))) {
			value = SqlTemplate.super.getById(entityClass, ids);
			if (value != null && cacheManager != null) {
				cacheManager.save(value);
			}
		}
		return value;
	}

	@Override
	public <T> boolean updatePart(Class<? extends T> entityClass, T entity) {
		if (SqlTemplate.super.updatePart(entityClass, entity)) {
			CacheManager cacheManager = getCacheManager();
			if (cacheManager != null) {
				cacheManager.delete(entityClass, entity);
			}
			return true;
		}
		return false;
	}
}
