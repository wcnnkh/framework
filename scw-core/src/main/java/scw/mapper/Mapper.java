package scw.mapper;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import scw.aop.ProxyUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.lang.NotFoundException;
import scw.util.cache.CacheLoader;
import scw.util.cache.CacheOperations;
import scw.util.cache.CacheUtils;
import scw.util.cache.LocalCacheType;

public abstract class Mapper {
	private transient final CacheOperations<Class<?>, FieldMetadata[]> cacheOperations;

	public Mapper(LocalCacheType localCacheType) {
		this.cacheOperations = CacheUtils.createLocalCache(localCacheType);
	}

	protected abstract CacheLoader<Class<?>, FieldMetadata[]> createCacheLoader(Class<?> clazz);

	public final FieldMetadata[] getFieldMetadatas(Class<?> clazz) {
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
				if (filterFeature != null && !filterFeature.getFilter().accept(field)) {
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
	
	public final LinkedList<Field> getFields(Class<?> clazz, FilterFeature... filterFeatures) {
		return getFields(clazz, true, null, null, filterFeatures);
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

	public final Field getField(Class<?> clazz, FieldFilter filter, FilterFeature... filterFeatures) {
		return getField(clazz, true, null, filter, filterFeatures);
	}

	public final Field getField(Class<?> clazz, boolean useSuperClass, final String name, final Class<?> type,
			Field parentField, final FieldFilter filter, final FilterFeature... filterFeatures) {
		return getField(clazz, useSuperClass, parentField, new FieldFilter() {

			public boolean accept(Field field) {
				if (!acceptInternal(field, filter, filterFeatures)) {
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
		return getField(clazz, true, name, type, null, null, filterFeatures);
	}

	public final Map<String, Object> getFieldValueMap(Object entity, final FieldFilter fieldFilter) {
		if (entity == null) {
			return Collections.emptyMap();
		}

		final Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Field field : getFields(ProxyUtils.getProxyFactory().getUserClass(entity.getClass()), null,
				new FieldFilter() {

					public boolean accept(Field field) {
						return field.isSupportGetter() && !Modifier.isStatic(field.getGetter().getModifiers())
								&& !map.containsKey(field.getGetter().getName()) && acceptInternal(field, fieldFilter);
					}
				})) {
			Object value = field.getGetter().get(entity);
			if (value == null) {
				continue;
			}

			map.put(field.getGetter().getName(), value);
		}
		return map;
	}

	public final Map<String, Object> getFieldValueMap(Object entity) {
		return getFieldValueMap(entity, null);
	}

	/**
	 * @param entity
	 * @param excludeNames
	 *            要排除的字段
	 * @return
	 */
	public final Map<String, Object> getFieldValueMapExcludeName(Object entity, final Set<String> excludeNames) {
		return getFieldValueMap(entity, new FieldFilter() {

			public boolean accept(Field field) {
				return CollectionUtils.isEmpty(excludeNames) || !excludeNames.contains(field.getGetter().getName());
			}
		});
	}

	public final Map<String, Object> getFieldValueMapExcludeName(Object entity, String... excludeNames) {
		Set<String> names;
		if (ArrayUtils.isEmpty(excludeNames)) {
			names = Collections.emptySet();
		} else {
			names = new HashSet<String>(Arrays.asList(excludeNames));
		}
		return getFieldValueMapExcludeName(entity, names);
	}

	/**
	 * 
	 * @param entity
	 * @param effectiveNames
	 *            要保留的字段
	 * @return
	 */
	public final Map<String, Object> getFieldValueMapEffectiveName(Object entity, final Set<String> effectiveNames) {
		return getFieldValueMap(entity, new FieldFilter() {

			public boolean accept(Field field) {
				return !CollectionUtils.isEmpty(effectiveNames) && effectiveNames.contains(field.getGetter().getName());
			}
		});
	}

	public final Map<String, Object> getFieldValueMapEffectiveName(Object entity, String... effectiveNames) {
		Set<String> names;
		if (ArrayUtils.isEmpty(effectiveNames)) {
			names = Collections.emptySet();
		} else {
			names = new HashSet<String>(Arrays.asList(effectiveNames));
		}
		return getFieldValueMapEffectiveName(entity, names);
	}

	@SuppressWarnings("rawtypes")
	public final <T> List<T> getFieldValueList(Collection entitys, final String fieldName) {
		return getFieldValueList(entitys, fieldName, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final <T> List<T> getFieldValueList(Collection entitys, final String fieldName,
			final Class<? extends T> type) {
		if (CollectionUtils.isEmpty(entitys)) {
			return Collections.emptyList();
		}

		Field field = null;
		List<T> list = new ArrayList<T>(entitys.size());
		for (Object entity : entitys) {
			if (entity == null) {
				continue;
			}

			if (field == null) {
				field = getField(ProxyUtils.getProxyFactory().getUserClass(entity.getClass()), new FieldFilter() {

					public boolean accept(Field field) {
						return field.isSupportGetter() && !Modifier.isStatic(field.getGetter().getModifiers())
								&& field.getGetter().getName().equals(fieldName)
								&& (type == null || type == field.getGetter().getType());
					}
				});

				if (field == null) {
					throw new NotFoundException(entity.getClass() + " [" + fieldName + "]");
				}
			}

			Object value = field.getGetter().get(entity);
			if (value == null) {
				continue;
			}

			list.add((T) value);
		}
		return list;
	}

	public final <T> T mapping(Class<? extends T> entityClass, Field parentField, Mapping mapping) throws Exception {
		return mapping.mapping(entityClass, getFields(entityClass, parentField, mapping, FilterFeature.SUPPORT_SETTER),
				this);
	}

	public final <T> T mapping(Class<? extends T> entityClass, Mapping mapping) throws Exception {
		return mapping(entityClass, null, mapping);
	}

	public final Enumeration<Field> enumeration(Class<?> entityClass, boolean useSuperClass, Field parentField,
			Collection<FieldFilter> fieldFilters, FilterFeature... filterFeatures) {
		return new EnumerationField(entityClass, useSuperClass, parentField, fieldFilters, filterFeatures);
	}

	public final Enumeration<Field> enumeration(Class<?> entityClass, boolean useSuperClass, Field parentField,
			FieldFilter... fieldFilters) {
		return enumeration(entityClass, useSuperClass, parentField, Arrays.asList(fieldFilters));
	}

	private final class EnumerationField implements Enumeration<Field>, FieldFilter {
		private Class<?> entityClass;
		private final boolean useSuperClass;
		private final Collection<FieldFilter> fieldFilters;
		private final Field parentField;
		private Enumeration<FieldMetadata> enumeration;
		private Field currentField;
		private final FilterFeature[] filterFeatures;

		public EnumerationField(Class<?> entityClass, boolean useSuperClass, Field parentField,
				Collection<FieldFilter> fieldFilters, FilterFeature[] filterFeatures) {
			this.entityClass = entityClass;
			this.fieldFilters = fieldFilters;
			this.useSuperClass = useSuperClass;
			this.parentField = parentField;
			this.filterFeatures = filterFeatures;
			this.enumeration = Collections.enumeration(Arrays.asList(getFieldMetadatas(entityClass)));
		}

		public boolean accept(Field field) {
			if (filterFeatures != null && filterFeatures.length != 0) {
				for (FilterFeature feature : filterFeatures) {
					if (feature != null && !feature.getFilter().accept(field)) {
						return false;
					}
				}
			}

			if (fieldFilters != null && fieldFilters.size() != 0) {
				for (FieldFilter filter : fieldFilters) {
					if (filter != null && !filter.accept(field)) {
						return false;
					}
				}
			}
			return true;
		}

		public boolean hasMoreElements() {
			if (currentField != null) {
				return true;
			}

			while (enumeration.hasMoreElements()) {
				FieldMetadata fieldMetadata = enumeration.nextElement();
				Field field = createField(parentField, fieldMetadata);
				if (EnumerationField.this.accept(field)) {
					this.currentField = field;
					return true;
				}
			}

			if (useSuperClass) {
				this.entityClass = entityClass.getSuperclass();
				if (entityClass == null || entityClass == Object.class) {
					return false;
				}

				this.enumeration = Collections.enumeration(Arrays.asList(getFieldMetadatas(entityClass)));
				while (enumeration.hasMoreElements()) {
					FieldMetadata fieldMetadata = enumeration.nextElement();
					Field field = createField(parentField, fieldMetadata);
					if (EnumerationField.this.accept(field)) {
						this.currentField = field;
						return true;
					}
				}
			}
			return false;
		}

		public Field nextElement() {
			if (currentField == null && !hasMoreElements()) {
				throw new NoSuchElementException();
			}

			try {
				return currentField.clone();
			} finally {
				this.currentField = null;
			}
		}
	}
}