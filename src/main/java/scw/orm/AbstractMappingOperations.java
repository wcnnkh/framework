package scw.orm;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.annotation.Ignore;
import scw.core.utils.CollectionUtils;
import scw.core.utils.IteratorCallback;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.orm.annotation.NotColumn;
import scw.orm.annotation.PrimaryKey;
import scw.orm.sql.SqlMapper;

public abstract class AbstractMappingOperations implements Mapper {
	static final char DEFAULT_CONNECTOR_CHARACTER = StringUtils
			.parseChar(SystemPropertyUtils.getProperty("orm.primary.key.connector.character"), ':');

	public abstract Collection<? extends SetterFilter> getSetterFilters();

	public abstract Collection<? extends GetterFilter> getGetterFilters();

	public void setter(MappingContext context, Object bean, Object value) throws Exception {
		setter(context, new FieldSetter(bean), value);
	}

	public void setter(MappingContext context, Setter setter, Object value) throws Exception {
		SetterFilterChain filterChain = new DefaultSetterFilterChain(getSetterFilters(), null);
		filterChain.setter(context, setter, value);
	}

	public Object getter(MappingContext context, Getter getter) throws Exception {
		GetterFilterChain filterChain = new DefaultGetterFilterChain(getGetterFilters(), null);
		return filterChain.getter(context, getter);
	}

	public Object getter(MappingContext context, Object bean) throws Exception {
		return getter(context, new FieldGetter(bean));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T> void create(Class<T> declaringClass, MappingContext superContext, Class<?> clazz, T bean,
			SetterMapping setterMapping) throws Exception {
		Map<String, Column> map = getColumnMap(clazz);
		for (Entry<String, Column> entry : map.entrySet()) {
			MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
			if (isIgnore(context)) {
				continue;
			}

			setterMapping.setter(context, bean, this);
		}

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			create(declaringClass, superContext, superClazz, bean, setterMapping);
		}
	}

	public <T> T create(MappingContext superContext, Class<T> clazz, SetterMapping<? extends Mapper> setterMapping)
			throws Exception {
		T bean = newInstance(clazz);
		create(clazz, superContext, clazz, bean, setterMapping);
		return bean;
	}

	public void iterator(MappingContext superContext, Class<?> clazz, IteratorMapping<? extends Mapper> iterator)
			throws Exception {
		iterator(clazz, superContext, clazz, iterator);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void iterator(Class<?> declaringClass, MappingContext superContext, Class<?> clazz,
			IteratorMapping iterator) throws Exception {
		Map<String, Column> map = getColumnMap(clazz);
		for (Entry<String, Column> entry : map.entrySet()) {
			MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
			if (isIgnore(context)) {
				continue;
			}

			iterator.iterator(context, this);
		}

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			iterator(declaringClass, superContext, superClazz, iterator);
		}
	}

	protected void appendMappingContexts(Class<?> declaringClass, MappingContext superContext, Class<?> clazz,
			List<MappingContext> list, IteratorCallback<MappingContext> filter) {
		Map<String, Column> map = getColumnMap(clazz);
		for (Entry<String, Column> entry : map.entrySet()) {
			MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
			if (isIgnore(context)) {
				continue;
			}

			if (filter == null || filter.iteratorCallback(context)) {
				list.add(context);
			}
		}

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			appendMappingContexts(declaringClass, superContext, superClazz, list, filter);
		}
	}

	public Collection<MappingContext> getMappingContexts(MappingContext superContext, Class<?> clazz,
			IteratorCallback<MappingContext> filter) {
		LinkedList<MappingContext> list = new LinkedList<MappingContext>();
		appendMappingContexts(clazz, superContext, clazz, list, filter);
		return list;
	}

	public Collection<MappingContext> getMappingContexts(Class<?> clazz, IteratorCallback<MappingContext> filter) {
		return getMappingContexts(null, clazz, filter);
	}

	public boolean isPrimaryKey(MappingContext mappingContext) {
		return mappingContext.getColumn().getAnnotation(PrimaryKey.class) != null;
	}

	public Collection<MappingContext> getPrimaryKeys(MappingContext supperContext, Class<?> clazz) {
		return getMappingContexts(supperContext, clazz, new IteratorCallback<MappingContext>() {

			public boolean iteratorCallback(MappingContext data) {
				return isPrimaryKey(data);
			}
		});
	}

	public Collection<MappingContext> getPrimaryKeys(Class<?> clazz) {
		return getPrimaryKeys(null, clazz);
	}

	protected void appendObjectKeyByValue(Appendable appendable, Object value) throws IOException {
		appendable.append(DEFAULT_CONNECTOR_CHARACTER);
		appendable.append(
				StringUtils.transferredMeaning(value == null ? null : value.toString(), DEFAULT_CONNECTOR_CHARACTER));
	}

	public <T> String getObjectKey(Class<? extends T> clazz, final T bean) {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		try {
			iterator(null, clazz, new IteratorMapping<SqlMapper>() {

				public void iterator(MappingContext context, SqlMapper mappingOperations) throws Exception {
					if (isPrimaryKey(context)) {
						appendObjectKeyByValue(sb, getter(context, bean));
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	public String getObjectKeyById(Class<?> clazz, Collection<Object> primaryKeys) {
		StringBuilder sb = new StringBuilder(128);
		sb.append(clazz.getName());
		Iterator<MappingContext> iterator = getPrimaryKeys(clazz).iterator();
		Iterator<Object> valueIterator = primaryKeys.iterator();
		while (iterator.hasNext() && valueIterator.hasNext()) {
			try {
				appendObjectKeyByValue(sb, getter(iterator.next(), new SimpleGetter(valueIterator.next())));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public <K> Map<String, K> getInIdKeyMap(Class<?> clazz, Collection<K> lastPrimaryKeys, Object[] primaryKeys) {
		if (CollectionUtils.isEmpty(lastPrimaryKeys)) {
			return Collections.EMPTY_MAP;
		}

		Map<String, K> keyMap = new HashMap<String, K>();
		Iterator<K> valueIterator = lastPrimaryKeys.iterator();

		while (valueIterator.hasNext()) {
			K k = valueIterator.next();
			Object[] ids;
			if (primaryKeys == null || primaryKeys.length == 0) {
				ids = new Object[] { k };
			} else {
				ids = new Object[primaryKeys.length];
				System.arraycopy(primaryKeys, 0, ids, 0, primaryKeys.length);
				ids[ids.length - 1] = valueIterator.next();
			}
			keyMap.put(getObjectKeyById(clazz, Arrays.asList(ids)), k);
		}
		return keyMap;
	}

	public boolean isIgnore(MappingContext context) {
		if (Modifier.isStatic(context.getColumn().getField().getModifiers())) {
			return true;
		}

		Ignore ignore = context.getColumn().getAnnotation(Ignore.class);
		if (ignore != null) {
			return true;
		}

		NotColumn exclude = context.getColumn().getAnnotation(NotColumn.class);
		if (exclude != null) {
			return true;
		}
		return false;
	}
}
