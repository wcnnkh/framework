package scw.orm;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.instance.CannotInstantiateException;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.FieldDefinition;

public class DefaultMappingOperations implements MappingOperations {
	private final FieldDefinitionFactory fieldDefinitionFactory;
	private final Collection<SetterFilter> setterFilters;
	private final Collection<GetterFilter> getterFilters;
	private final NoArgsInstanceFactory instanceFactory;

	public DefaultMappingOperations(FieldDefinitionFactory fieldDefinitionFactory,
			Collection<SetterFilter> setterFilters, Collection<GetterFilter> getterFilters,
			NoArgsInstanceFactory instanceFactory) {
		this.fieldDefinitionFactory = fieldDefinitionFactory;
		this.setterFilters = setterFilters;
		this.getterFilters = getterFilters;
		this.instanceFactory = instanceFactory;
	}

	public final FieldDefinitionFactory getFieldDefinitionFactory() {
		return fieldDefinitionFactory;
	}

	public void setter(MappingContext context, Object bean, Object value) throws Exception {
		SetterFilterChain filterChain = new DefaultSetterFilterChain(setterFilters, null);
		filterChain.setter(context, bean, value);
	}

	public Object getter(MappingContext context, Getter getter) throws Exception {
		GetterFilterChain filterChain = new DefaultGetterFilterChain(getterFilters, null);
		return filterChain.getter(context, getter);
	}

	private <T> void process(Class<T> declaringClass, MappingContext superContext, Class<?> clazz, T bean,
			Setter valueMapping) throws Exception {
		Map<String, FieldDefinition> map = fieldDefinitionFactory.getFieldDefinitionMap(clazz);
		while (map != null) {
			for (Entry<String, FieldDefinition> entry : map.entrySet()) {
				MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
				valueMapping.setter(context, bean, this);
			}

			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null && superClazz != Object.class) {
				process(declaringClass, superContext, superClazz, bean, valueMapping);
			}
		}
	}

	public <T> T create(MappingContext superContext, Class<T> clazz, Setter valueMapping) throws Exception {
		if (!instanceFactory.isInstance(clazz)) {
			throw new CannotInstantiateException("无法实例化：" + clazz);
		}

		T bean = instanceFactory.getInstance(clazz);
		process(clazz, superContext, clazz, bean, valueMapping);
		return bean;
	}

	public void iterator(MappingContext superContext, Class<?> clazz, IteratorMapping iterator) throws Exception {
		iterator(clazz, superContext, clazz, iterator);
	}

	private void iterator(Class<?> declaringClass, MappingContext superContext, Class<?> clazz,
			IteratorMapping iterator) throws Exception {
		Map<String, FieldDefinition> map = fieldDefinitionFactory.getFieldDefinitionMap(clazz);
		while (map != null) {
			for (Entry<String, FieldDefinition> entry : map.entrySet()) {
				iterator.iterator(new MappingContext(superContext, entry.getValue(), declaringClass), this);
			}

			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null && superClazz != Object.class) {
				iterator(clazz, superContext, superClazz, iterator);
			}
		}
	}

	public Object getter(MappingContext context, Object bean) throws Exception {
		return getter(context, new FieldGetter(bean));
	}
}
