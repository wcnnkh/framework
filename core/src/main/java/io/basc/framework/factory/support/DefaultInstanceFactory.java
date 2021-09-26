package io.basc.framework.factory.support;

import java.util.concurrent.ConcurrentMap;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.core.parameter.ParameterFactories;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.AbstractServiceLoaderFactory;
import io.basc.framework.factory.InstanceDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.NoArgsInstanceFactory;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.XUtils;
import io.basc.framework.value.ValueFactory;

@SuppressWarnings("unchecked")
public class DefaultInstanceFactory extends AbstractServiceLoaderFactory implements InstanceFactory {
	private ConcurrentMap<Class<?>, InstanceDefinition> cacheMap;
	private final Environment environment;
	private ClassLoaderProvider classLoaderProvider;
	private final ParameterFactories defaultValueFactory = new ParameterFactories();

	public DefaultInstanceFactory(Environment environment, boolean cache) {
		this.environment = environment;
		if (cache) {
			cacheMap = new ConcurrentReferenceHashMap<Class<?>, InstanceDefinition>();
		}
		defaultValueFactory.configure(this);
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
			return new InternalInstanceBuilder(clazz, clazz.cast(this));
		}

		if (Environment.class == clazz) {
			return new InternalInstanceBuilder(clazz, clazz.cast(environment));
		}

		if (ConversionService.class == clazz) {
			return new InternalInstanceBuilder(clazz, clazz.cast(environment.getConversionService()));
		}

		if (ParameterFactory.class == clazz) {
			return new InternalInstanceBuilder(clazz, clazz.cast(defaultValueFactory));
		}

		InstanceDefinition instanceBuilder = cacheMap == null ? null : (InstanceDefinition) cacheMap.get(clazz);
		if (instanceBuilder == null) {
			if (!XUtils.isAvailable(clazz)) {
				return null;
			}

			instanceBuilder = new DefaultInstanceDefinition(this, environment, clazz, this, defaultValueFactory);
			InstanceDefinition cache = cacheMap == null ? null
					: (InstanceDefinition) cacheMap.putIfAbsent(clazz, instanceBuilder);
			if (cache != null) {
				instanceBuilder = cache;
			}
		}
		return instanceBuilder;
	}

	private final class InternalInstanceBuilder extends DefaultInstanceDefinition {
		private final Object instance;

		public InternalInstanceBuilder(Class<?> targetClass, Object instance) {
			super(DefaultInstanceFactory.this, environment, targetClass, DefaultInstanceFactory.this,
					defaultValueFactory);
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
