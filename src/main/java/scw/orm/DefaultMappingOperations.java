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
		setter(context, new FieldSetter(bean), value);
	}

	public void setter(MappingContext context, Setter setter, Object value) throws Exception {
		SetterFilterChain filterChain = new DefaultSetterFilterChain(setterFilters, null);
		filterChain.setter(context, setter, value);
	}

	public Object getter(MappingContext context, Getter getter) throws Exception {
		GetterFilterChain filterChain = new DefaultGetterFilterChain(getterFilters, null);
		return filterChain.getter(context, getter);
	}

	public Object getter(MappingContext context, Object bean) throws Exception {
		return getter(context, new FieldGetter(bean));
	}

	private <T> void process(Class<T> declaringClass, MappingContext superContext, Class<?> clazz, T bean,
			SetterMapping setterMapping) throws Exception {
		Map<String, FieldDefinition> map = fieldDefinitionFactory.getFieldDefinitionMap(clazz);
		while (map != null) {
			for (Entry<String, FieldDefinition> entry : map.entrySet()) {
				MappingContext context = new MappingContext(superContext, entry.getValue(), declaringClass);
				setterMapping.setter(context, bean, this);
			}

			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null && superClazz != Object.class) {
				process(declaringClass, superContext, superClazz, bean, setterMapping);
			}
		}
	}

	public <T> T create(MappingContext superContext, Class<T> clazz, SetterMapping setterMapping) throws Exception {
		if (!instanceFactory.isInstance(clazz)) {
			throw new CannotInstantiateException("无法实例化：" + clazz);
		}

		T bean = instanceFactory.getInstance(clazz);
		process(clazz, superContext, clazz, bean, setterMapping);
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
}
