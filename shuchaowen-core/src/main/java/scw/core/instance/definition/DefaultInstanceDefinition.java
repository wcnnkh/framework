package scw.core.instance.definition;

import scw.core.instance.InstanceBuilder;
import scw.lang.UnsupportedException;

public class DefaultInstanceDefinition extends AbstractInstanceDefinition {
	private final InstanceBuilder instanceBuilder;

	public DefaultInstanceDefinition(Class<?> targetClass,
			InstanceBuilder instanceBuilder) {
		super(targetClass);
		this.instanceBuilder = instanceBuilder;
	}

	public InstanceBuilder getInstanceBuilder() {
		return instanceBuilder;
	}

	public boolean isInstance() {
		return instanceBuilder.getConstructor() != null;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() throws Exception {
		if (!isInstance()) {
			throw new UnsupportedException(getTargetClass().getName());
		}

		return (T) createInternal(getTargetClass(), getInstanceBuilder()
				.getConstructor(), getInstanceBuilder().getArgs());
	}
}
