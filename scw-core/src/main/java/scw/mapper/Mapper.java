package scw.mapper;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.aop.ProxyUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.ObjectUtils;
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

	public final Map<String, Object> getFieldValueMap(Object entity, final FieldFilter fieldFilter) {
		if (entity == null) {
			return Collections.emptyMap();
		}

		final Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Field field : getFields(ProxyUtils.getProxyFactory().getUserClass(entity.getClass()), true, null,
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
				field = getFields(ProxyUtils.getProxyFactory().getUserClass(entity.getClass()), true)
						.find(new FieldFilter() {

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
		return mapping.mapping(entityClass,
				getFields(entityClass, parentField, FilterFeature.SUPPORT_SETTER.getFilter(), mapping), this);
	}

	public final <T> T mapping(Class<? extends T> entityClass, Mapping mapping) throws Exception {
		return mapping(entityClass, null, mapping);
	}

	public final Fields getFields(Class<?> entityClass, FieldFilter... filters) {
		return getFields(entityClass, true, filters);
	}

	public final Fields getFields(Class<?> entityClass, Field parentField, FieldFilter... filters) {
		return getFields(entityClass, true, parentField, filters);
	}

	public final Fields getFields(Class<?> entityClass, boolean useSuperClass, FieldFilter... filters) {
		return getFields(entityClass, useSuperClass, null, filters);
	}

	public final Fields getFields(Class<?> entityClass, boolean useSuperClass, Field parentField,
			final FieldFilter... filters) {
		return getFields(entityClass, useSuperClass, parentField,
				ArrayUtils.isEmpty(filters) ? null : new FieldFilter() {
					public boolean accept(Field field) {
						for (FieldFilter fieldFilter : filters) {
							if (fieldFilter == null) {
								continue;
							}

							if (!fieldFilter.accept(field)) {
								return false;
							}
						}
						return true;
					}
				});
	}

	public final Fields getFields(Class<?> entityClass) {
		return getFields(entityClass, true, null, (FieldFilter) null);
	}

	public Fields getFields(Class<?> entityClass, boolean useSuperClass, Field parentField, FieldFilter filter) {
		return new IterableFields(entityClass, useSuperClass, filter, parentField) {

			@Override
			protected Iterator<FieldMetadata> getFieldMetadataIterator(Class<?> entityClass) {
				return Arrays.asList(getFieldMetadatas(entityClass)).iterator();
			}
		};
	}

	/**
	 * 验证一个对象所有的字段值
	 * 
	 * @param instance
	 * @param useSuperClass
	 * @param test
	 * @throws IllegalArgumentException
	 */
	public final void testFields(Object instance, boolean useSuperClass, FieldTest test)
			throws IllegalArgumentException {
		if (instance == null) {
			throw new IllegalArgumentException("Object cannot be empty");
		}

		for (scw.mapper.Field field : getFields(instance.getClass(), useSuperClass, FilterFeature.GETTER.getFilter())) {
			if (AnnotationUtils.isNullable(field.getGetter().getAnnotatedElement(), false)) {
				continue;
			}

			Object value = field.getGetter().get(instance);
			if(ObjectUtils.isEmpty(value)){
				throw new IllegalArgumentException(field.getGetter().toString());
			}

			if (!test.test(field, value)) {
				throw new IllegalArgumentException(field.getGetter().toString());
			}
		}
	}

	public final Fields getFields(Class<?> entityClass, boolean useSuperClass, Field parentField,
			final FilterFeature... filterFeatures) {
		return getFields(entityClass, useSuperClass, parentField,
				ArrayUtils.isEmpty(filterFeatures) ? null : new FieldFilter() {
					public boolean accept(Field field) {
						for (FilterFeature feature : filterFeatures) {
							if (feature == null) {
								continue;
							}

							if (!feature.getFilter().accept(field)) {
								return false;
							}
						}
						return true;
					}
				});
	}

	public final Fields getFields(Class<?> entityClass, Field parentField, final FilterFeature... filterFeatures) {
		return getFields(entityClass, true, parentField, filterFeatures);
	}

	public final Fields getFields(Class<?> entityClass, final FilterFeature... filterFeatures) {
		return getFields(entityClass, null, filterFeatures);
	}
}