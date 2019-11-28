package scw.orm;

import java.util.Collection;

import scw.core.instance.CannotInstantiateException;
import scw.core.instance.NoArgsInstanceFactory;

public class DefaultMapper extends AbstractMappingOperations {
	private final FieldDefinitionFactory fieldDefinitionFactory;
	private final Collection<? extends SetterFilter> setterFilters;
	private final Collection<? extends GetterFilter> getterFilters;
	private final NoArgsInstanceFactory instanceFactory;

	public DefaultMapper(FieldDefinitionFactory fieldDefinitionFactory,
			Collection<? extends SetterFilter> setterFilters, Collection<? extends GetterFilter> getterFilters,
			NoArgsInstanceFactory instanceFactory) {
		this.fieldDefinitionFactory = fieldDefinitionFactory;
		this.setterFilters = setterFilters;
		this.getterFilters = getterFilters;
		this.instanceFactory = instanceFactory;
	}

	public <T> T newInstance(Class<T> clazz) {
		if (!instanceFactory.isInstance(clazz)) {
			throw new CannotInstantiateException("无法实例化：" + clazz);
		}

		return instanceFactory.getInstance(clazz);
	}

	public FieldDefinitionFactory getFieldDefinitionFactory() {
		return fieldDefinitionFactory;
	}

	@Override
	public Collection<? extends SetterFilter> getSetterFilters() {
		return setterFilters;
	}

	@Override
	public Collection<? extends GetterFilter> getGetterFilters() {
		return getterFilters;
	}
}
