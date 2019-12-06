package scw.orm.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.lang.AlreadyExistsException;
import scw.orm.Mapper;
import scw.orm.MappingContext;
import scw.orm.ORMException;
import scw.orm.ObjectRelationalMapping;

@SuppressWarnings("unchecked")
public class DefaultObjectOperations implements ObjectOperations {
	private Mapper mapper;

	public DefaultObjectOperations(Mapper mapper) {
		this.mapper = mapper;
	}

	public Map<String, Object> getColumnValueMapExcludeName(ObjectRelationalMapping objectRelationalMapping, Object obj,
			Set<String> excludeNames) {
		if (obj == null) {
			return null;
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		for (MappingContext mappingContext : objectRelationalMapping) {
			if (excludeNames.contains(mappingContext.getColumn().getName())) {
				continue;
			}

			map.put(mappingContext.getColumn().getName(), getColumnValue(obj, mappingContext));
		}
		return map;
	}

	public Map<String, Object> getColumnValueMapEffectiveName(ObjectRelationalMapping objectRelationalMapping,
			Object obj, Collection<String> effectiveNames) {
		if (obj == null) {
			return null;
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> iterator = effectiveNames.iterator();
		while (iterator.hasNext()) {
			MappingContext context = objectRelationalMapping.getMappingContext(iterator.next());
			if (context == null) {
				continue;
			}

			map.put(context.getColumn().getName(), getColumnValue(obj, context));
		}
		return map;
	}

	public Map<String, Object> getColumnValueMapExcludeName(Object obj, Collection<String> excludeNames) {
		if (obj == null) {
			return Collections.EMPTY_MAP;
		}

		return getColumnValueMapExcludeName(mapper.getObjectRelationalMapping(ClassUtils.getUserClass(obj)), obj,
				CollectionUtils.asSet(excludeNames));
	}

	public Map<String, Object> getColumnValueMapEffectiveName(Object obj, Collection<String> effectiveNames) {
		if (obj == null) {
			return Collections.EMPTY_MAP;
		}

		return getColumnValueMapEffectiveName(mapper.getObjectRelationalMapping(ClassUtils.getUserClass(obj)), obj,
				effectiveNames);
	}

	public Map<String, Object> getObjectColumnValueMap(Object obj) {
		return getColumnValueMapExcludeName(obj, Collections.EMPTY_LIST);
	}

	public List<Map<String, Object>> getColumnValueMapExcludeNameByList(Collection<?> objs,
			Collection<String> excludeNames) {
		if (CollectionUtils.isEmpty(objs)) {
			return Collections.EMPTY_LIST;
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(objs.size());
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

	public List<Map<String, Object>> getColumnValueMapEffectiveNameByList(Collection<?> objs,
			Collection<String> effectiveNames) {
		if (CollectionUtils.isEmpty(objs)) {
			return Collections.EMPTY_LIST;
		}

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(objs.size());
		ObjectRelationalMapping mapping = getFirstObjectRelationalMapping(objs);
		Iterator<?> iterator = objs.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj == null) {
				continue;
			}

			list.add(getColumnValueMapEffectiveName(mapping, obj, effectiveNames));
		}
		return list;
	}

	public List<Map<String, Object>> getColumnValueMapEffectiveNameByList(Collection<?> objs) {
		return getColumnValueMapExcludeNameByList(objs, Collections.EMPTY_LIST);
	}

	public <E> E getColumnValue(Object obj, MappingContext context) {
		try {
			return (E) mapper.getter(context, obj);
		} catch (Exception e) {
			throw new ORMException(obj.getClass().getName(), e);
		}
	}

	private ObjectRelationalMapping getFirstObjectRelationalMapping(Collection<? extends Object> objs) {
		Iterator<? extends Object> iterator = objs.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj == null) {
				continue;
			}

			return mapper.getObjectRelationalMapping(ClassUtils.getUserClass(obj));
		}
		return null;
	}

	public <E> List<E> getColumnValueList(MappingContext context, Collection<?> objs) {
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

	private MappingContext getFirstMappingContext(Collection<? extends Object> objs) {
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

	public <E> List<E> getColumnValueList(Collection<?> objs) {
		MappingContext mappingContext = getFirstMappingContext(objs);
		if (mappingContext == null) {
			return Collections.EMPTY_LIST;
		}
		return getColumnValueList(mappingContext, objs);
	}

	public <K, V> Map<K, ? extends V> toMap(Collection<? extends V> objs, MappingContext context) {
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
				throw new AlreadyExistsException("[" + k + "] 已经存在 class=" + v.getClass().getName());
			}

			map.put(k, v);
		}
		return map;
	}

	public <K, V> Map<K, ? extends V> toMap(Collection<? extends V> objs, String name) {
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

	public <K, V> Map<K, ? extends V> toMap(Collection<? extends V> objs) {
		MappingContext mappingContext = getFirstMappingContext(objs);
		if (mappingContext == null) {
			return Collections.EMPTY_MAP;
		}

		return toMap(objs, mappingContext);
	}

}
