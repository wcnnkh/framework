package scw.mapper;

import java.util.LinkedList;

import scw.mapper.EntityMapping.Column;
import scw.util.cache.CacheLoader;
import scw.util.cache.CacheOperations;
import scw.util.cache.CacheUtils;
import scw.util.cache.LocalCacheType;

public abstract class FieldFactory {
	private final CacheOperations<Class<?>, Field[]> cacheOperations;

	public FieldFactory(LocalCacheType localCacheType) {
		this.cacheOperations = CacheUtils.createLocalCache(localCacheType);
	}

	protected abstract CacheLoader<Class<?>, Field[]> createCacheLoader(Class<?> clazz);

	protected final Field[] getFields(Class<?> clazz) {
		Field[] fields;
		try {
			if (cacheOperations.isExist(clazz)) {
				fields = cacheOperations.get(clazz);
			} else {
				fields = cacheOperations.get(clazz, createCacheLoader(clazz));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (fields == null || fields.length == 0) {
			return Field.EMPTY_ARRAY;
		}
		return fields.clone();
	}

	protected boolean acceptInternal(FieldContext fieldContext, FieldContextFilter filter,
			FilterFeature... fieldFilterTypes) {
		if (fieldFilterTypes != null && fieldFilterTypes.length != 0) {
			for (FilterFeature filterFeature : fieldFilterTypes) {
				if (!filterFeature.getFilter().accept(fieldContext)) {
					return false;
				}
			}
		}
		return filter == null || filter.accept(fieldContext);
	}

	public final LinkedList<FieldContext> getFieldContexts(Class<?> clazz, boolean useSuperClass,
			FieldContext parentContext, FieldContextFilter filter, FilterFeature... fieldFilterTypes) {
		LinkedList<FieldContext> list = new LinkedList<FieldContext>();
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.mapper.Field field : getFields(classToUse)) {
				FieldContext fieldContext = createFieldContext(parentContext, field, clazz);
				if (acceptInternal(fieldContext, filter, fieldFilterTypes)) {
					list.add(fieldContext);
				}
			}

			if (!useSuperClass) {
				break;
			}

			classToUse = classToUse.getSuperclass();
		}
		return list;
	}

	protected FieldContext createFieldContext(FieldContext parentContext, Field field, Class<?> declaringClass) {
		return new FieldContext(parentContext, field, declaringClass);
	}

	public final FieldContext getFieldContext(Class<?> clazz, boolean useSuperClass, FieldContext parentContext,
			FieldContextFilter filter, FilterFeature... fieldFilterTypes) {
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.mapper.Field field : getFields(classToUse)) {
				FieldContext fieldContext = createFieldContext(parentContext, field, clazz);
				if (acceptInternal(fieldContext, filter, fieldFilterTypes)) {
					return fieldContext;
				}
			}

			if (!useSuperClass) {
				break;
			}
			classToUse = classToUse.getSuperclass();
		}
		return null;
	}

	public final FieldContext getFieldContext(Class<?> clazz, boolean useSuperClass, String name, Class<?> type,
			FilterFeature... fieldFilterTypes) {
		return getFieldContext(clazz, useSuperClass, name, type, null, fieldFilterTypes);
	}

	public final FieldContext getFieldContext(Class<?> clazz, boolean useSuperClass, final String name, final Class<?> type,
			FieldContext parentContext, final FilterFeature... fieldFilterTypes) {
		return getFieldContext(clazz, useSuperClass, parentContext, new FieldContextFilter() {

			public boolean accept(FieldContext fieldContext) {
				if (!acceptInternal(fieldContext, null, fieldFilterTypes)) {
					return false;
				}

				if (fieldContext.getField().isSupportGetter()) {
					if ((type == null || fieldContext.getField().getGetter().getType() == type) && fieldContext.getField().getGetter().getName().equals(name)) {
						return true;
					}
				}

				if (fieldContext.getField().isSupportSetter()) {
					if ((type == null || fieldContext.getField().getSetter().getType() == type) && fieldContext.getField().getSetter().getName().equals(name)) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public final FieldContext getFieldContext(Class<?> clazz, FieldContext parentContext, FieldContextFilter filter,
			FilterFeature... fieldFilterTypes) {
		return getFieldContext(clazz, true, parentContext, filter, fieldFilterTypes);
	}

	public final FieldContext getFieldContext(Class<?> clazz, String name, Class<?> type, FieldContext parentContext,
			FilterFeature... fieldFilterTypes) {
		return getFieldContext(clazz, true, name, type, parentContext, fieldFilterTypes);
	}

	public final LinkedList<FieldContext> getFieldContexts(Class<?> clazz, FieldContext parentContext,
			FieldContextFilter filter, FilterFeature... fieldFilterTypes) {
		return getFieldContexts(clazz, true, parentContext, filter, fieldFilterTypes);
	}

	public final FieldContext getFieldContext(Class<?> clazz, String name, Class<?> type, FilterFeature... fieldFilterTypes) {
		return getFieldContext(clazz, true, name, type, fieldFilterTypes);
	}

	public final <T> T mapping(Class<? extends T> entityClass, FieldContext parentContext, Mapping mapping) throws Exception {
		T entity = mapping.newInstance(entityClass);
		for (FieldContext fieldContext : getFieldContexts(entityClass, parentContext, mapping,
				FilterFeature.SUPPORT_SETTER)) {
			Object value = mapping.mapping(entityClass, fieldContext, this);
			if (value == null) {
				continue;
			}

			fieldContext.getField().getSetter().set(entity, value);
		}
		return entity;
	}

	public final EntityMapping getEntityMapping(Class<?> entityClass, FieldContext parentContext, Mapper mapper) {
		if (entityClass == null || entityClass == Object.class) {
			return null;
		}

		LinkedList<FieldContext> fieldContexts = getFieldContexts(entityClass, false, parentContext, mapper);
		Column[] columns = new Column[fieldContexts.size()];
		int index = 0;
		for (FieldContext fieldContext : fieldContexts) {
			EntityMapping getterEntityMapping = null;
			if (fieldContext.getField().isSupportGetter() && mapper.isEntity(fieldContext.getField().getGetter())) {
				getterEntityMapping = getEntityMapping(fieldContext.getField().getGetter().getType(), fieldContext,
						mapper);
			}
			EntityMapping setterEntityMapping = null;
			if (fieldContext.getField().isSupportSetter() && mapper.isEntity(fieldContext.getField().getSetter())) {
				setterEntityMapping = getEntityMapping(fieldContext.getField().getSetter().getType(), fieldContext,
						mapper);
			}

			columns[index++] = new Column(fieldContext, getterEntityMapping, setterEntityMapping);
		}
		return new EntityMapping(entityClass, columns,
				getEntityMapping(entityClass.getSuperclass(), parentContext, mapper));
	}

	public final <T> T mapping(Class<? extends T> entityClass, FieldContext parentContext, Mapper mapper)
			throws Exception {
		return mapper.mapping(entityClass, getEntityMapping(entityClass, parentContext, mapper), this);
	}
}