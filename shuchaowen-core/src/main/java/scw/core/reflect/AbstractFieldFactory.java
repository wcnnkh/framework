package scw.core.reflect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import scw.util.cache.CacheLoader;
import scw.util.cache.CacheOperations;
import scw.util.cache.CacheUtils;
import scw.util.cache.LocalCacheType;

public abstract class AbstractFieldFactory implements FieldFactory {
	private final CacheOperations<Class<?>, Collection<Field>> cacheOperations;

	public AbstractFieldFactory(LocalCacheType localCacheType) {
		this.cacheOperations = CacheUtils.createLocalCache(localCacheType);
	}

	protected abstract CacheLoader<Class<?>, Collection<Field>> createCacheLoader(
			Class<?> clazz);

	public Collection<Field> getFields(Class<?> clazz) {
		Collection<Field> fields;
		try {
			if(cacheOperations.isExist(clazz)){
				fields = cacheOperations.get(clazz);
			}else{
				fields = cacheOperations.get(clazz, createCacheLoader(clazz));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if(fields == null){
			return Collections.emptyList();
		}
		
		return Collections.unmodifiableCollection(fields);
	}

	protected boolean acceptInternal(FieldContext fieldContext,
			FieldContextFilter filter) {
		return filter == null || filter.accept(fieldContext);
	}

	public Collection<FieldContext> getFieldContexts(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter) {
		List<FieldContext> list = new LinkedList<FieldContext>();
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.core.reflect.Field field : getFields(classToUse)) {
				FieldContext fieldContext = new FieldContext(parentContext,
						field, clazz);
				if (acceptInternal(fieldContext, filter)) {
					list.add(fieldContext);
				}
			}
			classToUse = clazz.getSuperclass();
		}
		return list;
	}

	public FieldContext getFieldContext(Class<?> clazz,
			FieldContext parentContext, FieldContextFilter filter) {
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.core.reflect.Field field : getFields(classToUse)) {
				FieldContext fieldContext = new FieldContext(parentContext,
						field, clazz);
				if (acceptInternal(fieldContext, filter)) {
					return fieldContext;
				}
			}
			classToUse = clazz.getSuperclass();
		}
		return null;
	}
}
