package scw.orm;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.instance.CannotInstantiateException;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.FieldDefinition;

public final class DefaultORMOperations implements ORMOperations {
	private final FieldDefinitionFactory fieldDefinitionFactory;
	private final Collection<SetterFilter> setterFilters;
	private final Collection<GetterFilter> getterFilters;
	private final NoArgsInstanceFactory instanceFactory;

	public DefaultORMOperations(FieldDefinitionFactory fieldDefinitionFactory, Collection<SetterFilter> setterFilters,
			Collection<GetterFilter> getterFilters, NoArgsInstanceFactory instanceFactory) {
		this.fieldDefinitionFactory = fieldDefinitionFactory;
		this.setterFilters = setterFilters;
		this.getterFilters = getterFilters;
		this.instanceFactory = instanceFactory;
	}

	public final FieldDefinitionFactory getFieldDefinitionFactory() {
		return fieldDefinitionFactory;
	}

	public void setter(FieldDefinitionContext context, Object bean, Object value) throws Exception {
		SetterFilterChain filterChain = new DefaultSetterFilterChain(setterFilters, null);
		filterChain.setter(context, bean, value);
	}

	public Object getter(FieldDefinitionContext context, Object bean) throws Exception {
		GetterFilterChain filterChain = new DefaultGetterFilterChain(getterFilters, null);
		return filterChain.getter(context, bean);
	}

	private void process(FieldDefinitionContext superContext, Class<?> clazz, Object bean, ValueFactory valueFactory)
			throws Exception {
		Map<String, FieldDefinition> map = fieldDefinitionFactory.getFieldDefinitionMap(clazz);
		while (map != null) {
			for (Entry<String, FieldDefinition> entry : map.entrySet()) {
				FieldDefinitionContext context = new FieldDefinitionContext(superContext, entry.getValue());
				setter(context, bean, valueFactory.getValue(context, this));
			}

			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null && superClazz != Object.class) {
				process(superContext, superClazz, bean, valueFactory);
			}
		}
	}

	public <T> T create(FieldDefinitionContext superContext, Class<T> clazz, ValueFactory valueFactory) throws Exception {
		if (!instanceFactory.isInstance(clazz)) {
			throw new CannotInstantiateException("无法实例化：" + clazz);
		}

		T bean = instanceFactory.getInstance(clazz);
		process(superContext, clazz, bean, valueFactory);
		return bean;
	}

	public void iterator(FieldDefinitionContext superContext, Class<?> clazz, FieldDefintionIterator iterator)
			throws Exception {
		Map<String, FieldDefinition> map = fieldDefinitionFactory.getFieldDefinitionMap(clazz);
		while (map != null) {
			for (Entry<String, FieldDefinition> entry : map.entrySet()) {
				iterator.iterator(new FieldDefinitionContext(superContext, entry.getValue()), this);
			}

			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null && superClazz != Object.class) {
				iterator(superContext, superClazz, iterator);
			}
		}
	}
}
