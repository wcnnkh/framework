package scw.instance.factory;

import scw.core.utils.ClassUtils;
import scw.env.Environment;
import scw.instance.InstanceBuilder;
import scw.instance.support.DefaultInstanceBuilder;
import scw.util.ConcurrentReferenceHashMap;

public class DefaultInstanceFactory extends AbstractInstanceFactory {
	private final ConcurrentReferenceHashMap<String, InstanceBuilder<?>> builderMap = new ConcurrentReferenceHashMap<String, InstanceBuilder<?>>();
	private final Environment environment;
	private ClassLoader classLoader;

	public DefaultInstanceFactory(Environment environment) {
		this.environment = environment;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> InstanceBuilder<T> getInstanceBuilder(String name) {
		InstanceBuilder<T> instanceBuilder = (InstanceBuilder<T>) builderMap.get(name);
		if (instanceBuilder == null) {
			Class<T> clazz = (Class<T>) ClassUtils.forNameNullable(name, getClassLoader());
			if (clazz == null) {
				return null;
			}

			if (ClassUtils.isAssignableValue(clazz, this)) {
				return new InternalInstanceBuilder<T>(this, environment, clazz, clazz.cast(this));
			}

			if (Environment.class == clazz) {
				return new InternalInstanceBuilder<T>(this, environment, clazz, clazz.cast(environment));
			}

			if (isIgnoreClass(clazz)) {
				return null;
			}

			instanceBuilder = new DefaultInstanceBuilder<T>(this, environment, clazz);
		}

		return instanceBuilder;
	}

	private static final class InternalInstanceBuilder<T> extends DefaultInstanceBuilder<T> {
		private final T instance;

		public InternalInstanceBuilder(NoArgsInstanceFactory instanceFactory, Environment environment,
				Class<T> targetClass, T instance) {
			super(instanceFactory, environment, targetClass);
			this.instance = instance;
		}

		public boolean isInstance() {
			return true;
		}

		public T create() throws Exception {
			return instance;
		}
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
	}
}
