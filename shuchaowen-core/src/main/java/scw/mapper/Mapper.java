package scw.mapper;

import java.util.LinkedList;
import java.util.List;

import scw.mapper.EntityMapping.Column;
import scw.util.cache.CacheLoader;
import scw.util.cache.CacheOperations;
import scw.util.cache.CacheUtils;
import scw.util.cache.LocalCacheType;

public abstract class Mapper {
	private final CacheOperations<Class<?>, FieldMetadata[]> cacheOperations;

	public Mapper(LocalCacheType localCacheType) {
		this.cacheOperations = CacheUtils.createLocalCache(localCacheType);
	}

	protected abstract CacheLoader<Class<?>, FieldMetadata[]> createCacheLoader(Class<?> clazz);

	protected final FieldMetadata[] getFieldMetadatas(Class<?> clazz) {
		FieldMetadata[] fieldMetadatas;
		try {
			if (cacheOperations.isExist(clazz)) {
				fieldMetadatas = cacheOperations.get(clazz);
			} else {
				fieldMetadatas = cacheOperations.get(clazz, createCacheLoader(clazz));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (fieldMetadatas == null || fieldMetadatas.length == 0) {
			return FieldMetadata.EMPTY_ARRAY;
		}
		return fieldMetadatas.clone();
	}

	protected boolean acceptInternal(Field field, FieldFilter filter, FilterFeature... filterFeatures) {
		if (filterFeatures != null && filterFeatures.length != 0) {
			for (FilterFeature filterFeature : filterFeatures) {
				if (!filterFeature.getFilter().accept(field)) {
					return false;
				}
			}
		}
		return filter == null || filter.accept(field);
	}

	public final LinkedList<Field> getFields(Class<?> clazz, boolean useSuperClass, Field parentField,
			FieldFilter filter, FilterFeature... filterFeatures) {
		LinkedList<Field> list = new LinkedList<Field>();
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (FieldMetadata fieldMetadata : getFieldMetadatas(classToUse)) {
				Field field = createField(parentField, fieldMetadata);
				if (acceptInternal(field, filter, filterFeatures)) {
					list.add(field);
				}
			}

			if (!useSuperClass) {
				break;
			}

			classToUse = classToUse.getSuperclass();
		}
		return list;
	}
	
	public final LinkedList<Field> getFields(Class<?> clazz, Field parentField, FieldFilter filter,
			FilterFeature... filterFeatures) {
		return getFields(clazz, true, parentField, filter, filterFeatures);
	}

	protected Field createField(Field parentField, FieldMetadata fieldMetadata) {
		return new Field(parentField, fieldMetadata.getGetter(), fieldMetadata.getSetter());
	}

	public final Field getField(Class<?> clazz, boolean useSuperClass, Field parentField, FieldFilter filter,
			FilterFeature... filterFeatures) {
		Class<?> classToUse = clazz;
		while (classToUse != null && classToUse != Object.class) {
			for (FieldMetadata fieldMetadata : getFieldMetadatas(classToUse)) {
				Field field = createField(parentField, fieldMetadata);
				if (acceptInternal(field, filter, filterFeatures)) {
					return field;
				}
			}

			if (!useSuperClass) {
				break;
			}
			classToUse = classToUse.getSuperclass();
		}
		return null;
	}
	
	public final Field getField(Class<?> clazz, FieldFilter filter,
			FilterFeature... filterFeatures) {
		return getField(clazz, true, null, filter, filterFeatures);
	}

	public final Field getField(Class<?> clazz, boolean useSuperClass, final String name, final Class<?> type,
			Field parentField, final FilterFeature... filterFeatures) {
		return getField(clazz, useSuperClass, parentField, new FieldFilter() {

			public boolean accept(Field field) {
				if (!acceptInternal(field, null, filterFeatures)) {
					return false;
				}

				if (field.isSupportGetter()) {
					if ((type == null || field.getGetter().getType() == type)
							&& field.getGetter().getName().equals(name)) {
						return true;
					}
				}

				if (field.isSupportSetter()) {
					if ((type == null || field.getSetter().getType() == type)
							&& field.getSetter().getName().equals(name)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	public final Field getField(Class<?> clazz, String name, Class<?> type, FilterFeature... filterFeatures) {
		return getField(clazz, true, name, type, null, filterFeatures);
	}

	public final <T> T mapping(Class<? extends T> entityClass, Field parentField, Mapping mapping) throws Exception {
		T entity = mapping.newInstance(entityClass);
		for (Field field : getFields(entityClass, parentField, mapping, FilterFeature.SUPPORT_SETTER)) {
			Object value = mapping.mapping(entityClass, field, this);
			if (value == null) {
				continue;
			}

			field.getSetter().set(entity, value);
		}
		return entity;
	}

	public final <T> T mapping(Class<? extends T> entityClass, Mapping mapping) throws Exception {
		return mapping(entityClass, null, mapping);
	}

	public final EntityMapping getEntityMapping(Class<?> entityClass, Field parentField,
			EntityResolver entityResolver) {
		if (entityClass == null || entityClass == Object.class) {
			return null;
		}

		List<Column> columns = new LinkedList<EntityMapping.Column>();
		for (FieldMetadata fieldMetadata : getFieldMetadatas(entityClass)) {
			Field field = createField(parentField, fieldMetadata);
			if (acceptInternal(field, entityResolver)) {
				EntityMapping getterEntityMapping = null;
				if (field.isSupportGetter() && entityResolver.isEntity(field.getGetter())) {
					getterEntityMapping = getEntityMapping(field.getGetter().getType(), field, entityResolver);
				}
				EntityMapping setterEntityMapping = null;
				if (field.isSupportSetter() && entityResolver.isEntity(field.getSetter())) {
					setterEntityMapping = getEntityMapping(field.getSetter().getType(), field, entityResolver);
				}
				columns.add(new Column(field, getterEntityMapping, setterEntityMapping));
			}
		}
		return new EntityMapping(columns, getEntityMapping(entityClass.getSuperclass(), parentField, entityResolver));
	}

	public final EntityMapping getEntityMapping(Class<?> entityClass, EntityResolver entityResolver) {
		return getEntityMapping(entityClass, null, entityResolver);
	}
}