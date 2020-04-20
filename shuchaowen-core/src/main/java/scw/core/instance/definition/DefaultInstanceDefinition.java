package scw.core.instance.definition;

import scw.core.instance.ConstructorBuilder;
import scw.core.instance.InstanceFactory;
import scw.lang.UnsupportedException;

public class DefaultInstanceDefinition extends AbstractInstanceDefinition {
	private final ConstructorBuilder instanceBuilder;

	public DefaultInstanceDefinition(Class<?> targetClass,
			ConstructorBuilder instanceBuilder, InstanceFactory instanceFactory) {
		super(targetClass, instanceFactory);
		this.instanceBuilder = instanceBuilder;
	}

	public ConstructorBuilder getInstanceBuilder() {
		return instanceBuilder;
	}

	public boolean isInstance() {
		return instanceBuilder.getConstructor() != null;
	}

	public Object create() throws Exception {
		if (!isInstance()) {
			throw new UnsupportedException(getTargetClass().getName());
		}

		return createInternal(getTargetClass(), getInstanceBuilder()
				.getConstructor(), getInstanceBuilder().getArgs());
	}

	public boolean isSingleton() {
		return false;
	}
}
