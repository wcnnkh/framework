package io.basc.framework.factory.support;

import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Creator;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Return;
import io.basc.framework.util.Source;

public class DefaultInstanceFactory extends DefaultSingletonRegistry implements InstanceFactory {
	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		Assert.requiredArgument(classLoader != null, "classLoader");
		this.classLoader = classLoader;
	}

	public <T, E extends Throwable> T getInstance(BeanDefinition definition, Creator<? extends T, ? extends E> creater)
			throws E {
		return getInstance(definition, creater, true).get();
	}

	public <T, E extends Throwable> Return<T> getInstance(BeanDefinition definition,
			Creator<? extends T, ? extends E> creater, boolean postProcessBean) throws E {
		if (definition.isSingleton()) {
			return getSingleton(definition.getId(), creater, postProcessBean);
		}

		T instance = creater.create();
		if (postProcessBean) {
			processPostBean(instance, definition);
		}
		return Return.success(instance);
	}

	@SuppressWarnings("unchecked")
	public <T, E extends Throwable> Return<T> getInstance(String name,
			Processor<? super BeanDefinition, ? extends T, ? extends E> createProcessor, boolean postProcessBean)
			throws E {
		Object object = getSingleton(name);
		if (object != null) {
			return Return.success((T) object);
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		return getInstance(definition, () -> createProcessor.process(definition), postProcessBean);
	}

	public <T, E extends Throwable> T getInstance(String name, Processor<BeanDefinition, T, E> createProcessor)
			throws E {
		return getInstance(name, createProcessor, true).get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getInstance(Class<? extends T> clazz) throws FactoryException {
		return (T) getInstance(clazz.getName());
	}

	@Override
	public Object getInstance(String name) throws FactoryException {
		return getInstance(name, (e) -> e.create());
	}

	@Override
	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	public <E extends Throwable> boolean isInstance(BeanDefinition definition, Source<Boolean, E> processor)
			throws E {
		if (definition == null) {
			return false;
		}

		if (containsSingleton(definition.getId())) {
			return true;
		}

		Boolean b = processor.get();
		return b == null ? false : b;
	}

	public <E extends Throwable> boolean isInstance(String name, Processor<BeanDefinition, Boolean, E> processor)
			throws E {
		if (containsSingleton(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		return isInstance(definition, () -> processor.process(definition));
	}

	@Override
	public boolean isInstance(String name) {
		return isInstance(name, (e) -> e.isInstance());
	}

	public boolean isSingleton(Class<?> clazz) {
		return isSingleton(clazz.getName());
	}

	public boolean isSingleton(String name) {
		if (containsSingleton(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return containsSingleton(definition.getId()) || definition.isSingleton();
	}
}
