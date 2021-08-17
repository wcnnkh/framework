package scw.instance.support;

import java.util.concurrent.ConcurrentMap;

import scw.core.utils.ClassUtils;
import scw.env.Environment;
import scw.instance.AbstractServiceLoaderFactory;
import scw.instance.InstanceDefinition;
import scw.instance.InstanceException;
import scw.instance.InstanceFactory;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoaderFactory;
import scw.util.ClassLoaderProvider;
import scw.util.ConcurrentReferenceHashMap;
import scw.util.DefaultClassLoaderProvider;
import scw.util.XUtils;
import scw.value.ValueFactory;

@SuppressWarnings("unchecked")
public class DefaultInstanceFactory extends AbstractServiceLoaderFactory implements InstanceFactory {
	private ConcurrentMap<Class<?>, InstanceDefinition> cacheMap;
	private final Environment environment;
	private ClassLoaderProvider classLoaderProvider;

	public DefaultInstanceFactory(Environment environment, boolean cache) {
		this.environment = environment;
		if (cache) {
			cacheMap = new ConcurrentReferenceHashMap<Class<?>, InstanceDefinition>();
		}
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public void setClassLoader(ClassLoader classLoader) {
		setClassLoaderProvider(new DefaultClassLoaderProvider(classLoader));
	}

	@Override
	public ClassLoader getClassLoader() {
		if (classLoaderProvider == null) {
			return ClassUtils.getClassLoader(environment);
		}
		return ClassUtils.getClassLoader(classLoaderProvider);
	}

	public <T> T getInstance(Class<T> clazz) {
		InstanceDefinition instanceBuilder = getDefinition(clazz);
		if (instanceBuilder == null) {
			return null;
		}
		return (T) instanceBuilder.create();
	}

	public <T> T getInstance(String name) {
		InstanceDefinition instanceBuilder = getDefinition(name);
		if (instanceBuilder == null) {
			return null;
		}

		return (T) instanceBuilder.create();
	}

	public boolean isInstance(String name) {
		InstanceDefinition instanceBuilder = getDefinition(name);
		if (instanceBuilder == null) {
			return false;
		}

		return instanceBuilder.isInstance();
	}

	public boolean isInstance(Class<?> clazz) {
		InstanceDefinition instanceBuilder = getDefinition(clazz);
		if (instanceBuilder == null) {
			return false;
		}

		return instanceBuilder.isInstance();
	}

	public boolean isInstance(String name, Object... params) {
		InstanceDefinition instanceBuilder = getDefinition(name);
		if (instanceBuilder == null) {
			return false;
		}

		return instanceBuilder.isInstance(params);
	}

	public <T> T getInstance(String name, Object... params) {
		InstanceDefinition instanceBuilder = getDefinition(name);
		if (instanceBuilder == null) {
			return null;
		}

		return (T) instanceBuilder.create(params);
	}

	public boolean isInstance(Class<?> type, Object... params) {
		InstanceDefinition instanceBuilder = getDefinition(type);
		if (instanceBuilder == null) {
			return false;
		}

		return instanceBuilder.isInstance(params);
	}

	public <T> T getInstance(Class<T> type, Object... params) {
		InstanceDefinition instanceBuilder = getDefinition(type);
		if (instanceBuilder == null) {
			return null;
		}

		return (T) instanceBuilder.create(params);
	}

	public boolean isInstance(String name, Class<?>[] parameterTypes) {
		InstanceDefinition instanceBuilder = getDefinition(name);
		if (instanceBuilder == null) {
			return false;
		}

		return instanceBuilder.isInstance(parameterTypes);
	}

	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object[] params) {
		InstanceDefinition instanceBuilder = getDefinition(name);
		if (instanceBuilder == null) {
			return null;
		}

		return (T) instanceBuilder.create(parameterTypes, params);
	}

	public boolean isInstance(Class<?> type, Class<?>[] parameterTypes) {
		InstanceDefinition instanceBuilder = getDefinition(type);
		if (instanceBuilder == null) {
			return false;
		}
		return instanceBuilder.isInstance(parameterTypes);
	}

	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object[] params) {
		InstanceDefinition instanceBuilder = getDefinition(type);
		if (instanceBuilder == null) {
			return null;
		}

		return (T) instanceBuilder.create(parameterTypes, params);
	}

	public InstanceDefinition getDefinition(String name) {
		Class<?> type = ClassUtils.getClass(name, getClassLoader());
		if (type == null) {
			return null;
		}

		return getDefinition(type);
	}

	public InstanceDefinition getDefinition(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		if (ClassUtils.isAssignableValue(clazz, this)) {
			return new InternalInstanceBuilder(this, environment, clazz, this, clazz.cast(this));
		}

		if (Environment.class == clazz) {
			return new InternalInstanceBuilder(this, environment, clazz, this, clazz.cast(environment));
		}

		InstanceDefinition instanceBuilder = cacheMap == null ? null : (InstanceDefinition) cacheMap.get(clazz);
		if (instanceBuilder == null) {
			if (!XUtils.isAvailable(clazz)) {
				return null;
			}

			instanceBuilder = new DefaultInstanceDefinition(this, environment, clazz, this);
			InstanceDefinition cache = cacheMap == null ? null
					: (InstanceDefinition) cacheMap.putIfAbsent(clazz, instanceBuilder);
			if (cache != null) {
				instanceBuilder = cache;
			}
		}
		return instanceBuilder;
	}

	private static final class InternalInstanceBuilder extends DefaultInstanceDefinition {
		private final Object instance;

		public InternalInstanceBuilder(NoArgsInstanceFactory instanceFactory, Environment environment,
				Class<?> targetClass, ServiceLoaderFactory serviceLoaderFactory, Object instance) {
			super(instanceFactory, environment, targetClass, serviceLoaderFactory);
			this.instance = instance;
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws InstanceException {
			return instance;
		}
	}

	@Override
	protected final ValueFactory<String> getConfigFactory() {
		return environment;
	}

	@Override
	protected final NoArgsInstanceFactory getTargetInstanceFactory() {
		return this;
	}
}
