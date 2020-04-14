package scw.core.instance.definition;

import scw.lang.UnsupportedException;

public class DefaultInstanceDefinition extends AbstractInstanceDefinition {
	private final ConstructorDefinition constructorDefinition;

	public DefaultInstanceDefinition(Class<?> targetClass,
			ConstructorDefinition constructorDefinition) {
		super(targetClass);
		this.constructorDefinition = constructorDefinition;
	}

	public ConstructorDefinition getConstructorDefinition() {
		return constructorDefinition;
	}

	public boolean isInstance() {
		return getConstructorDefinition().getConstructor() != null;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() throws Exception {
		if (!isInstance()) {
			throw new UnsupportedException(getTargetClass().getName());
		}

		return (T) createInternal(getTargetClass(), getConstructorDefinition()
				.getConstructor(), getConstructorDefinition().getArgs());
	}
}
