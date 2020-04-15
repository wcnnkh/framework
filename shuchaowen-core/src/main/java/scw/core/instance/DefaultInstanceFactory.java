package scw.core.instance;

import scw.core.instance.definition.AutoInstanceDefinition;
import scw.core.instance.definition.InstanceDefinition;
import scw.core.parameter.DefaultParameterDescriptorFactory;
import scw.core.parameter.ParameterDescriptorFactory;
import scw.core.utils.ClassUtils;
import scw.util.ConcurrentReferenceHashMap;
import scw.util.value.property.MultiPropertyFactory;
import scw.util.value.property.PropertyFactory;

public class DefaultInstanceFactory extends
		AbstractInstanceFactory<InstanceDefinition> {
	protected final ConcurrentReferenceHashMap<String, InstanceDefinition> definitionMap = new ConcurrentReferenceHashMap<String, InstanceDefinition>();
	private final MultiPropertyFactory propertyFactory = new MultiPropertyFactory();
	private final ParameterDescriptorFactory parameterDescriptorFactory = new DefaultParameterDescriptorFactory();
	private final boolean singletion;

	public DefaultInstanceFactory(PropertyFactory propertyFactory,
			boolean singletion) {
		super();
		singletonMap.put(PropertyFactory.class.getName(), this.propertyFactory);
		if (propertyFactory != null) {
			this.propertyFactory.add(propertyFactory);
		}
		this.singletion = singletion;
	}

	public MultiPropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	@Override
	public InstanceDefinition getDefinition(String name) {
		InstanceDefinition definition = definitionMap.get(name);
		if (definition != null) {
			return definition;
		}

		Class<?> clazz = ClassUtils.forNameNullable(name);
		if (clazz == null) {
			return null;
		}

		definition = new AutoInstanceDefinition(clazz, this,
				getPropertyFactory(), parameterDescriptorFactory, singletion);
		definitionMap.put(definition.getId(), definition);
		return definition;
	}
}
