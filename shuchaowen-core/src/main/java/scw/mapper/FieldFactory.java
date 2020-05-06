package scw.mapper;

import java.util.LinkedList;

import scw.util.cache.CacheLoader;
import scw.util.cache.CacheOperations;
import scw.util.cache.CacheUtils;
import scw.util.cache.LocalCacheType;

public abstract class FieldFactory {
	private final CacheOperations<Class<?>, Field[]> cacheOperations;

	public FieldFactory(LocalCacheType localCacheType) {
		this.cacheOperations = CacheUtils.createLocalCache(localCacheType);
	}

	protected abstract CacheLoader<Class<?>, Field[]> createCacheLoader(
			Class<?> clazz);

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

	protected boolean acceptInternal(FieldContext fieldContext,
			FieldContextFilter filter, FieldFilterType... fieldFilterTypes) {
		if (fieldFilterTypes != null && fieldFilterTypes.length != 0) {
			for (FieldFilterType fieldFilterType : fieldFilterTypes) {
				if (!fieldFilterType.getFilter().accept(fieldContext)) {
					return false;
				}
			}
		}
		return filter == null || filter.accept(fieldContext);
	}

	public final LinkedList<FieldContext> getFieldContexts(Class<?> clazz,
			boolean useSuperClass, FieldContext parentContext,
			FieldContextFilter filter, FieldFilterType... fieldFilterTypes) {
		LinkedList<FieldContext> list = new LinkedList<FieldContext>();
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.mapper.Field field : getFields(classToUse)) {
				FieldContext fieldContext = createFieldContext(parentContext,
						field, clazz);
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

	protected FieldContext createFieldContext(FieldContext parentContext,
			Field field, Class<?> declaringClass) {
		return new FieldContext(parentContext, field, declaringClass);
	}

	public final FieldContext getFieldContext(Class<?> clazz,
			boolean useSuperClass, FieldContext parentContext,
			FieldContextFilter filter, FieldFilterType... fieldFilterTypes) {
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.mapper.Field field : getFields(classToUse)) {
				FieldContext fieldContext = createFieldContext(parentContext,
						field, clazz);
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

	public final FieldContext getFieldContext(Class<?> clazz,
			boolean useSuperClass, String name,
			FieldFilterType... fieldFilterTypes) {
		return getFieldContext(clazz, useSuperClass, name, null,
				fieldFilterTypes);
	}

	public final FieldContext getFieldContext(Class<?> clazz,
			boolean useSuperClass, final String name,
			FieldContext parentContext,
			final FieldFilterType... fieldFilterTypes) {
		return getFieldContext(clazz, useSuperClass, parentContext,
				new FieldContextFilter() {

					public boolean accept(FieldContext fieldContext) {
						if (!acceptInternal(fieldContext, null,
								fieldFilterTypes)) {
							return false;
						}

						if (fieldContext.getField().isSupportGetter()) {
							if (fieldContext.getField().getGetter().getName()
									.equals(name)) {
								return true;
							}
						}

						if (fieldContext.getField().isSupportSetter()) {
							if (fieldContext.getField().getSetter().getName()
									.equals(name)) {
								return true;
							}
						}
						return false;
					}
				});
	}

	public final FieldContext getFieldContext(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter,
			FieldFilterType... fieldFilterTypes) {
		return getFieldContext(clazz, true, parentContext, filter,
				fieldFilterTypes);
	}

	public final FieldContext getFieldContext(Class<?> clazz, String name,
			FieldContext parentContext, FieldFilterType... fieldFilterTypes) {
		return getFieldContext(clazz, true, name, parentContext,
				fieldFilterTypes);
	}

	public final LinkedList<FieldContext> getFieldContexts(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter,
			FieldFilterType... fieldFilterTypes) {
		return getFieldContexts(clazz, true, parentContext, filter,
				fieldFilterTypes);
	}

	public final FieldContext getFieldContext(Class<?> clazz, String name,
			FieldFilterType... fieldFilterTypes) {
		return getFieldContext(clazz, true, name, fieldFilterTypes);
	}

	public final EntityMapping getEntityMapping(Class<?> entityClass,
			boolean useSuperClass) {
		if (useSuperClass) {
			Class<?> classToUse = entityClass.getSuperclass();
			if (classToUse == null || classToUse == Object.class) {
				return getEntityMapping(entityClass, false);
			} else {
				return new EntityMapping(entityClass, getFields(entityClass),
						getEntityMapping(classToUse, useSuperClass));
			}
		} else {
			return new EntityMapping(entityClass, getFields(entityClass), null);
		}
	}
}