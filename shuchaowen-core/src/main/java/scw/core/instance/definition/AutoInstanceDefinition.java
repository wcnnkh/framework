package scw.core.instance.definition;

import scw.core.instance.InstanceFactory;
import scw.core.parameter.ParameterDescriptorFactory;
import scw.util.value.property.PropertyFactory;

public class AutoInstanceDefinition extends DefaultInstanceDefinition {
	private boolean singletion;

	public AutoInstanceDefinition(Class<?> targetClass,
			InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			ParameterDescriptorFactory parameterDescriptorFactory,
			boolean singletion) {
		super(targetClass, new AutoConstructorDefinition(instanceFactory,
				propertyFactory, targetClass, parameterDescriptorFactory));
		this.singletion = singletion;
	}

	@Override
	public boolean isSingleton() {
		return singletion;
	}
}
