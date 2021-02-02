package scw.instance.support;

import scw.instance.InstanceDefinition;
import scw.instance.InstanceFactory;

@SuppressWarnings("unchecked")
public abstract class AbstractInstanceFactory extends AbstractNoArgsInstanceFactory implements InstanceFactory {
	
	public <T> T getInstance(Class<T> clazz) {
		InstanceDefinition instanceBuilder = getDefinition(clazz);
		if(instanceBuilder == null){
			
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
}
