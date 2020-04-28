package scw.orm.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.aop.ProxyUtils;
import scw.core.Assert;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.CollectionUtils;
import scw.lang.AlreadyExistsException;
import scw.lang.ParameterVerifyException;
import scw.orm.ColumnFactory;
import scw.orm.Mapper;
import scw.orm.MappingContext;
import scw.orm.ORMException;
import scw.orm.ORMUtils;
import scw.orm.ObjectRelationalMapping;

@Configuration(order = Integer.MIN_VALUE)
@SuppressWarnings("unchecked")
public class DefaultObjectOperations implements ObjectOperations {
	private Mapper mapper;

	public DefaultObjectOperations() {
		ColumnFactory columnFactory = new CacheColumnFactoryWrapper(
				new MethodColumnFactory(GlobalPropertyFactory.getInstance()));
		this.mapper = new DefaultMapper(columnFactory, ORMUtils.getFilters(),
				ORMUtils.getFilters(), ORMUtils.getInstanceFactory());
	}

	public DefaultObjectOperations(Mapper mapper) {
		this.mapper = mapper;
	}

	public Map<String, Object> getColumnValueMapExcludeName(
			ObjectRelationalMapping objectRelationalMapping, Object obj,
			Set<String> excludeNames) {
		if (obj == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		for (MappingContext mappingContext : objectRelationalMapping) {
			if (!mappingContext.getColumn().isSupportGet()) {
				continue;
			}

			if (excludeNames.contains(mappingContext.getColumn().getName())) {
				continue;
			}

			map.put(mappingContext.getColumn().getName(),
					getColumnValue(obj, mappingContext));
		}
		return map;
	}

	public Map<String, Object> getColumnValueMapEffectiveName(
			ObjectRelationalMapping objectRelationalMapping, Object obj,
			Collection<String> effectiveNames) {
		if (obj == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iterator = effectiveNames.iterator();
		while (iterator.hasNext()) {
			MappingContext context = objectRelationalMapping
					.getMappingContext(iterator.next());
			if (context == null) {
				continue;
			}

			if (!context.getColumn().isSupportGet()) {
				continue;
			}

			map.put(context.getColumn().getName(), getColumnValue(obj, context));
		}
		return map;
	}

	public Map<String, Object> getColumnValueMapExcludeName(Object obj,
			Collection<String> excludeNames) {
		if (obj == null) {
			return Collections.EMPTY_MAP;
		}

		return getColumnValueMapExcludeName(
				mapper.getObjectRelationalMapping(ProxyUtils.getProxyAdapter().getUserClass(obj.getClass())),
				obj, CollectionUtils.asSet(excludeNames));
	}

	public Map<String, Object> getColumnValueMapEffectiveName(Object obj,
			Collection<String> effectiveNames) {
		if (obj == null) {
			return Collections.EMPTY_MAP;
		}

		return getColumnValueMapEffectiveName(
				mapper.getObjectRelationalMapping(ProxyUtils.getProxyAdapter().getUserClass(obj.getClass())),
				obj, effectiveNames);
	}

	public Map<String, Object> getColumnValueMap(Object obj) {
		return getColumnValueMapExcludeName(obj, Collections.EMPTY_LIST);
	}

	public List<Map<String, Object>> getColumnValueListMapExcludeName(
			Collection<?> objs, Collection<String> excludeNames) {
		if (CollectionUtils.isEmpty(objs)) {
			return Collections.EMPTY_LIST;
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(
				objs.size());
		ObjectRelationalMapping mapping = getFirstObjectRelationalMapping(objs);
		Iterator<?> iterator = objs.iterator();
		Set<String> excludeNameSet = CollectionUtils.asSet(excludeNames);
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj == null) {
				continue;
			}

			list.add(getColumnValueMapExcludeName(mapping, obj, excludeNameSet));
		}
		return list;
	}

	public List<Map<String, Object>> getColumnValueListMapEffectiveName(
			Collection<?> objs, Collection<String> effectiveNames) {
		if (CollectionUtils.isEmpty(objs)) {
			return Collections.EMPTY_LIST;
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(
				objs.size());
		ObjectRelationalMapping mapping = getFirstObjectRelationalMapping(objs);
		Iterator<?> iterator = objs.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj == null) {
				continue;
			}

			list.add(getColumnValueMapEffectiveName(mapping, obj,
					effectiveNames));
		}
		return list;
	}

	public List<Map<String, Object>> getColumnValueListMap(Collection<?> objs) {
		return getColumnValueListMapExcludeName(objs, Collections.EMPTY_LIST);
	}

	public <E> E getColumnValue(Object obj, MappingContext context) {
		try {
			return (E) mapper.getter(context, obj);
		} catch (Exception e) {
			throw new ORMException(obj.getClass().getName(), e);
		}
	}

	private ObjectRelationalMapping getFirstObjectRelationalMapping(
			Collection<? extends Object> objs) {
		Iterator<? extends Object> iterator = objs.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj == null) {
				continue;
			}

			return mapper.getObjectRelationalMapping(ProxyUtils.getProxyAdapter().getUserClass(obj.getClass()));
		}
		return null;
	}

	public <E> List<E> getColumnValueList(MappingContext context,
			Collection<?> objs) {
		List<E> list = new ArrayList<E>(objs.size());
		Iterator<?> iterator = objs.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj == null) {
				continue;
			}

			E e = getColumnValue(obj, context);
			if (e == null) {
				continue;
			}

			list.add(e);
		}
		return list;
	}

	public <E> List<E> getColumnValueList(Collection<?> objs, String name) {
		if (CollectionUtils.isEmpty(objs)) {
			return Collections.EMPTY_LIST;
		}

		ObjectRelationalMapping mapping = getFirstObjectRelationalMapping(objs);
		MappingContext context = mapping.getMappingContext(name);
		if (context == null) {
			return Collections.EMPTY_LIST;
		}

		return getColumnValueList(context, objs);
	}

	private MappingContext getFirstMappingContext(
			Collection<? extends Object> objs) {
		if (CollectionUtils.isEmpty(objs)) {
			return null;
		}

		ObjectRelationalMapping mapping = getFirstObjectRelationalMapping(objs);
		List<MappingContext> list = mapping.getPrimaryKeys();
		if (list.size() == 1) {
			return list.get(0);
		}

		if (!list.isEmpty()) {
			return list.get(0);
		}

		list = mapping.getNotPrimaryKeys();
		if (!list.isEmpty()) {
			return list.get(0);
		}

		list = mapping.getEntitys();
		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	public <E> List<E> getColumnValueListByFirstPrimary(Collection<?> objs) {
		MappingContext mappingContext = getFirstMappingContext(objs);
		if (mappingContext == null) {
			return Collections.EMPTY_LIST;
		}
		return getColumnValueList(mappingContext, objs);
	}

	public <K, V> Map<K, ? extends V> toMap(Collection<? extends V> objs,
			MappingContext context) {
		Map<K, V> map = new LinkedHashMap<K, V>();
		Iterator<? extends V> iterator = objs.iterator();
		while (iterator.hasNext()) {
			V v = iterator.next();
			if (v == null) {
				continue;
			}

			K k = getColumnValue(v, context);
			if (k == null) {
				continue;
			}

			if (map.containsKey(k)) {
				throw new AlreadyExistsException("[" + k + "] 已经存在 class="
						+ v.getClass().getName());
			}

			map.put(k, v);
		}
		return map;
	}

	public <K, V> Map<K, ? extends V> toMap(Collection<? extends V> objs,
			String name) {
		if (CollectionUtils.isEmpty(objs)) {
			return Collections.EMPTY_MAP;
		}

		ObjectRelationalMapping mapping = getFirstObjectRelationalMapping(objs);
		MappingContext context = mapping.getMappingContext(name);
		if (context == null) {
			return Collections.EMPTY_MAP;
		}

		return toMap(objs, context);
	}

	public <K, V> Map<K, ? extends V> toMapByFirstPrimary(
			Collection<? extends V> objs) {
		MappingContext mappingContext = getFirstMappingContext(objs);
		if (mappingContext == null) {
			return Collections.EMPTY_MAP;
		}

		return toMap(objs, mappingContext);
	}

	protected void verify(ObjectRelationalMapping objectRelationalMapping,
			Object obj) {
		for (MappingContext mappingContext : objectRelationalMapping) {
			if (!mappingContext.getColumn().isSupportGet()) {
				continue;
			}

			if (mapper.isEntity(mappingContext)) {
				verify(mappingContext.getColumn().get(obj));
			} else {
				if (!mapper.isNullable(mappingContext)) {
					Object value = mappingContext.getColumn().get(obj);
					if (value == null) {
						throw new ParameterVerifyException(mappingContext
								.getColumn().getName());
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void verify(Object obj) {
		Assert.requiredArgument(obj != null, "obj");

		if (obj instanceof Collection) {
			Assert.notEmpty((Collection) obj);
		} else if (obj instanceof Map) {
			Assert.notEmpty((Map) obj);
		} else if (obj.getClass().isArray()) {
			Assert.isTrue(Array.getLength(obj) == 0);
		} else {
			verify(mapper.getObjectRelationalMapping(ProxyUtils.getProxyAdapter().getUserClass(obj.getClass())), obj);
		}
	}

	public void verify(Collection<Object> objs) {
		Assert.notEmpty(objs);
		for (Object obj : objs) {
			verify(obj);
		}
	}

}
