package scw.mapper;

import java.util.LinkedList;

import scw.mapper.EntityMapping.Column;
import scw.util.cache.CacheLoader;
import scw.util.cache.CacheOperations;
import scw.util.cache.CacheUtils;
import scw.util.cache.LocalCacheType;

public class EntityManager implements CacheLoader<Class<?>, EntityMapping> {
	static final FilterFeature[] FIELD_FILTER_TYPES = new FilterFeature[] { FilterFeature.SUPPORT_SETTER,
			FilterFeature.SETTER_IGNORE_STATIC };
	private final FieldFactory fieldFactory;
	private final FieldContextFilter fieldContextFilter;
	private final CacheOperations<Class<?>, EntityMapping> cacheOperations;

	public EntityManager(LocalCacheType localCacheType) {
		this(null, localCacheType);
	}

	public EntityManager(FieldContextFilter fieldContextFilter, LocalCacheType localCacheType) {
		this(MapperUtils.getFieldFactory(), fieldContextFilter, localCacheType);
	}

	public EntityManager(FieldFactory fieldFactory, FieldContextFilter fieldContextFilter,
			LocalCacheType localCacheType) {
		this.fieldFactory = fieldFactory;
		this.fieldContextFilter = fieldContextFilter;
		this.cacheOperations = CacheUtils.createLocalCache(localCacheType);
	}

	public boolean isEntity(FieldDescriptor fieldDescriptor) {
		return false;
	}

	public final EntityMapping getEntityMapping(Class<?> entityClass) {
		try {
			if (cacheOperations.isExist(entityClass)) {
				return cacheOperations.get(entityClass);
			} else {
				return cacheOperations.get(entityClass, this);
			}
		} catch (Exception e) {
			throw new RuntimeException(entityClass.getName(), e);
		}
	}

	protected EntityMapping loader(Class<?> entityClass, FieldContext parentContext) {
		if (entityClass == null || entityClass == Object.class) {
			return null;
		}

		LinkedList<FieldContext> fieldContexts = fieldFactory.getFieldContexts(entityClass, false, parentContext,
				fieldContextFilter, FIELD_FILTER_TYPES);
		Column[] columns = new Column[fieldContexts.size()];
		int index = 0;
		for (FieldContext fieldContext : fieldFactory.getFieldContexts(entityClass, false, parentContext,
				fieldContextFilter, FIELD_FILTER_TYPES)) {
			EntityMapping getterEntityMapping = null;
			if (fieldContext.getField().isSupportGetter() && isEntity(fieldContext.getField().getGetter())) {
				getterEntityMapping = loader(fieldContext.getField().getGetter().getType(), fieldContext);
			}
			EntityMapping setterEntityMapping = null;
			if (fieldContext.getField().isSupportSetter() && isEntity(fieldContext.getField().getSetter())) {
				setterEntityMapping = loader(fieldContext.getField().getSetter().getType(), fieldContext);
			}

			columns[index++] = new Column(fieldContext, getterEntityMapping, setterEntityMapping);
		}
		return new EntityMapping(entityClass, columns, loader(entityClass.getSuperclass(), parentContext));

	}

	public final EntityMapping loader(Class<?> entityClass) throws Exception {
		return loader(entityClass, null);
	}
}
