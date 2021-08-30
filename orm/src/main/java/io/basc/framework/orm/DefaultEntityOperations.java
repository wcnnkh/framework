package io.basc.framework.orm;

import io.basc.framework.json.JSONUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.orm.cache.CacheManager;
import io.basc.framework.orm.generator.DefaultGeneratorProcessor;
import io.basc.framework.orm.generator.GeneratorProcessor;
import io.basc.framework.orm.sql.SqlTemplate;

import java.util.Arrays;

public class DefaultEntityOperations implements EntityOperations{
	private static Logger logger = LoggerFactory.getLogger(DefaultEntityOperations.class);
	private CacheManager cacheManager;
	private GeneratorProcessor generatorProcessor;
	private EntityOperations entityOperations;
	
	public DefaultEntityOperations(SqlTemplate sqlTemplate) {
		this(sqlTemplate.getSqlDialect(), sqlTemplate, sqlTemplate);
	}
	
	public DefaultEntityOperations(ObjectRelationalMapping objectRelationalMapping, MaxValueFactory maxValueFactory, EntityOperations entityOperations) {
		this.generatorProcessor = new DefaultGeneratorProcessor(objectRelationalMapping, maxValueFactory);
		this.entityOperations = entityOperations;
	}
	
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
		this.generatorProcessor = generatorProcessor;
	}

	@Override
	public <T> boolean save(Class<? extends T> entityClass, T entity) {
		generatorProcessor.process(entityClass, entity);
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.save(entityClass, entity)) {
				logger.error("save [{}] to cache error: {}", entityClass,
						JSONUtils.getJsonSupport().toJSONString(entity));
				return false;
			}
		}
		return entityOperations.save(entityClass, entity);
	}

	@Override
	public <T> boolean delete(Class<? extends T> entityClass, T entity) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.delete(entity)) {
				logger.error("delete [{}] to cache error: {}", entityClass,
						JSONUtils.getJsonSupport().toJSONString(entity));
				return false;
			}
		}
		return entityOperations.delete(entityClass, entity);
	}

	@Override
	public boolean deleteById(Class<?> entityClass, Object... ids) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.deleteById(entityClass, ids)) {
				logger.error("deleteById [{}] to cache error: {}", entityClass, Arrays.toString(ids));
				return false;
			}
		}
		return entityOperations.deleteById(entityClass, ids);
	}

	@Override
	public <T> boolean update(Class<? extends T> entityClass, T entity) {
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.update(entityClass, entity)) {
				logger.error("update [{}] to cache error: {}", entityClass, entity);
				return false;
			}
		}
		return entityOperations.update(entityClass, entity);
	}

	@Override
	public <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		generatorProcessor.process(entityClass, entity);
		CacheManager cacheManager = getCacheManager();
		if (cacheManager != null) {
			if (!cacheManager.saveOrUpdate(entityClass, entity)) {
				logger.error("saveOrUpdate [{}] to cache error: {}", entityClass,
						JSONUtils.getJsonSupport().toJSONString(entity));
				return false;
			}
		}
		return entityOperations.saveOrUpdate(entityClass, entity);
	}

	@Override
	public <T> T getById(Class<? extends T> entityClass, Object... ids) {
		CacheManager cacheManager = getCacheManager();
		T value = null;
		if (cacheManager != null) {
			value = cacheManager.getById(entityClass, ids);
		}

		if (cacheManager == null || (value == null && cacheManager.isKeepLooking(entityClass, ids))) {
			value = entityOperations.getById(entityClass, ids);
			if (value != null && cacheManager != null) {
				cacheManager.save(value);
			}
		}
		return value;
	}

}
