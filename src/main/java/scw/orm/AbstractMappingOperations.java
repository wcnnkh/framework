package scw.orm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.reflect.FieldDefinition;
import scw.core.utils.IteratorCallback;

public abstract class AbstractMappingOperations implements Mapper {

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
		Map<String, FieldDefinition> map = getFieldDefinitionFactory().getFieldDefinitionMap(clazz);
		for (Entry<String, FieldDefinition> entry : map.entrySet()) {
			MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
			setterMapping.setter(context, bean, this);
		}

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			create(declaringClass, superContext, superClazz, bean, setterMapping);
		}
	}

	public <T> T create(MappingContext superContext, Class<T> clazz,
			SetterMapping<? extends Mapper> setterMapping) throws Exception {
		T bean = newInstance(clazz);
		create(clazz, superContext, clazz, bean, setterMapping);
		return bean;
	}

	public void iterator(MappingContext superContext, Class<?> clazz,
			IteratorMapping<? extends Mapper> iterator) throws Exception {
		iterator(clazz, superContext, clazz, iterator);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void iterator(Class<?> declaringClass, MappingContext superContext, Class<?> clazz,
			IteratorMapping iterator) throws Exception {
		Map<String, FieldDefinition> map = getFieldDefinitionFactory().getFieldDefinitionMap(clazz);
		for (Entry<String, FieldDefinition> entry : map.entrySet()) {
			iterator.iterator(new MappingContext(superContext, entry.getValue(), declaringClass), this);
		}

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && superClazz != Object.class) {
			iterator(declaringClass, superContext, superClazz, iterator);
		}
	}

	protected void appendMappingContexts(Class<?> declaringClass, MappingContext superContext, Class<?> clazz,
			List<MappingContext> list, IteratorCallback<MappingContext> filter) {
		Map<String, FieldDefinition> map = getFieldDefinitionFactory().getFieldDefinitionMap(clazz);
		for (Entry<String, FieldDefinition> entry : map.entrySet()) {
			MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
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
}
