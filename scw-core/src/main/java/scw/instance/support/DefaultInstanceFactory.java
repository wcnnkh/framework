package scw.instance.support;

import java.util.concurrent.ConcurrentMap;

import scw.core.utils.ClassUtils;
import scw.env.Environment;
import scw.instance.InstanceDefinition;
import scw.instance.InstanceException;
import scw.instance.InstanceUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.instance.ServiceLoaderFactory;
import scw.util.ConcurrentReferenceHashMap;

public class DefaultInstanceFactory extends AbstractInstanceFactory implements ServiceLoaderFactory {
	private ConcurrentMap<Class<?>, InstanceDefinition> cacheMap;
	private final Environment environment;

	public DefaultInstanceFactory(Environment environment, boolean cache) {
		setClassLoaderProvider(environment);
		this.environment = environment;
		if(cache){
			cacheMap = new ConcurrentReferenceHashMap<Class<?>, InstanceDefinition>();
		}
	}
	
	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return InstanceUtils.getServiceLoader(serviceClass, this, environment);
	}

	public InstanceDefinition getDefinition(String name) {
		Class<?> type = getClass(name);
		if(type == null){
			return null;
		}
		
		return getDefinition(type);
	}
	
	public InstanceDefinition getDefinition(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		
		if (ClassUtils.isAssignableValue(clazz, this)) {
			return new InternalInstanceBuilder(this, environment, clazz, clazz.cast(this));
		}
		
		if (Environment.class == clazz) {
			return new InternalInstanceBuilder(this, environment, clazz, clazz.cast(environment));
		}
		
		InstanceDefinition instanceBuilder = cacheMap == null? null:(InstanceDefinition) cacheMap.get(clazz);
		if (instanceBuilder == null) {
			if (!isPresent(clazz)) {
				return null;
			}
			
			instanceBuilder = new DefaultInstanceDefinition(this, environment, clazz);
			InstanceDefinition cache = cacheMap == null? null:(InstanceDefinition) cacheMap.putIfAbsent(clazz, instanceBuilder);
			if(cache != null){
				instanceBuilder = cache;
			}
		}
		return instanceBuilder;
	}
	
	private static final class InternalInstanceBuilder extends DefaultInstanceDefinition {
		private final Object instance;

		public InternalInstanceBuilder(NoArgsInstanceFactory instanceFactory, Environment environment,
				Class<?> targetClass, Object instance) {
			super(instanceFactory, environment, targetClass);
			this.instance = instance;
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws InstanceException {
			return instance;
		}
	}
}
