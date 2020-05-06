package scw.core.reflect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import scw.util.cache.CacheLoader;
import scw.util.cache.CacheOperations;
import scw.util.cache.CacheUtils;
import scw.util.cache.LocalCacheType;

public abstract class AbstractFieldFactory implements FieldFactory {
	private final CacheOperations<Class<?>, Collection<Field>> cacheOperations;

	public AbstractFieldFactory(LocalCacheType localCacheType) {
		this.cacheOperations = CacheUtils.createLocalCache(localCacheType);
	}

	protected abstract CacheLoader<Class<?>, Collection<Field>> createCacheLoader(Class<?> clazz);

	public Collection<Field> getFields(Class<?> clazz) {
		Collection<Field> fields;
		try {
			if (cacheOperations.isExist(clazz)) {
				fields = cacheOperations.get(clazz);
			} else {
				fields = cacheOperations.get(clazz, createCacheLoader(clazz));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (fields == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableCollection(fields);
	}

	protected boolean acceptInternal(FieldContext fieldContext, FieldContextFilter filter, FieldFilterType ...fieldFilterTypes) {
		if(fieldFilterTypes != null && fieldFilterTypes.length != 0){
			for(FieldFilterType fieldFilterType : fieldFilterTypes){
				if(!fieldFilterType.getFilter().accept(fieldContext)){
					return false;
				}
			}
		}
		return filter == null || filter.accept(fieldContext);
	}

	public LinkedList<FieldContext> getFieldContexts(Class<?> clazz, FieldContext parentContext,
			FieldContextFilter filter, FieldFilterType ...fieldFilterTypes) {
		LinkedList<FieldContext> list = new LinkedList<FieldContext>();
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.core.reflect.Field field : getFields(classToUse)) {
				FieldContext fieldContext = new FieldContext(parentContext, field, clazz);
				if (acceptInternal(fieldContext, filter, fieldFilterTypes)) {
					list.add(fieldContext);
				}
			}
			classToUse = clazz.getSuperclass();
		}
		return list;
	}

	public FieldContext getFieldContext(Class<?> clazz, FieldContext parentContext, FieldContextFilter filter, FieldFilterType ...fieldFilterTypes) {
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (scw.core.reflect.Field field : getFields(classToUse)) {
				FieldContext fieldContext = new FieldContext(parentContext, field, clazz);
				if (acceptInternal(fieldContext, filter, fieldFilterTypes)) {
					return fieldContext;
				}
			}
			classToUse = clazz.getSuperclass();
		}
		return null;
	}

	public FieldContext getFieldContext(Class<?> clazz, String name, FieldFilterType ...fieldFilterTypes) {
		return getFieldContext(clazz, name, null, fieldFilterTypes);
	}

	public FieldContext getFieldContext(Class<?> clazz, final String name, FieldContext parentContext, final FieldFilterType ...fieldFilterTypes) {
		return getFieldContext(clazz, parentContext, new FieldContextFilter() {
			
			public boolean accept(FieldContext fieldContext) {
				if(!acceptInternal(fieldContext, null, fieldFilterTypes)){
					return false;
				}
				
				if(fieldContext.getField().isSupportGetter()){
					if(fieldContext.getField().getGetter().getName().equals(name)){
						return true;
					}
					
					if(fieldContext.getField().getGetter().getDisplayName().equals(name)){
						return true;
					}
				}
				
				if(fieldContext.getField().isSupportSetter()){
					if(fieldContext.getField().getSetter().getName().equals(name)){
						return true;
					}
					
					if(fieldContext.getField().getSetter().getDisplayName().equals(name)){
						return true;
					}
				}
				return false;
			}
		});
	}
}
