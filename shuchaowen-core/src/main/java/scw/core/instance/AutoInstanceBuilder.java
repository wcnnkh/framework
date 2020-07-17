package scw.core.instance;

import java.lang.reflect.Constructor;

import scw.value.property.PropertyFactory;

public class AutoInstanceBuilder<T> extends AbstractInstanceBuilder<T> {
	private ConstructorBuilder constructorBuilder;

	public AutoInstanceBuilder(Class<T> targetClass, NoArgsInstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		super(targetClass);
		this.constructorBuilder = new AutoConstructorBuilder(instanceFactory, propertyFactory, targetClass);
	}

	public boolean isInstance() {
		return constructorBuilder.getConstructor() != null;
	}

	@SuppressWarnings("unchecked")
	public T create() throws Exception {
		return createInternal(getTargetClass(), (Constructor<T>) constructorBuilder.getConstructor(),
				constructorBuilder.getArgs());
	}

}
