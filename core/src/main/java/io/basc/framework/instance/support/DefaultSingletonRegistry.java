package io.basc.framework.instance.support;

import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.instance.SingletonRegistry;
import io.basc.framework.util.Creator;
import io.basc.framework.util.DefaultStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultSingletonRegistry implements SingletonRegistry {
	private volatile Map<String, Object> singletionMap = new LinkedHashMap<String, Object>();

	public void registerSingleton(String beanName, Object singletonObject) {
		synchronized (singletionMap) {
			Object old = singletionMap.get(beanName);
			if (old != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '"
						+ beanName + "': there is already object [" + old + "] bound");
			}

			singletionMap.put(beanName, singletonObject);
		}
	}

	public Object getSingleton(String beanName) {
		return singletionMap.get(beanName);
	}

	public boolean containsSingleton(String beanName) {
		return singletionMap.containsKey(beanName);
	}

	public String[] getSingletonNames() {
		synchronized (singletionMap) {
			return StringUtils.toStringArray(singletionMap.keySet());
		}
	}

	public void removeSingleton(String name) {
		synchronized (singletionMap) {
			singletionMap.remove(name);
		}
	}

	public Object getSingletonMutex() {
		return singletionMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, E extends Throwable> DefaultStatus<T> getSingleton(String name, Creator<T, E> creater) throws E {
		T object = (T) singletionMap.get(name);
		if (object == null) {
			synchronized (singletionMap) {
				object = (T) singletionMap.get(name);
				if (object == null) {
					object = creater.create();
					registerSingleton(name, object);
					return new DefaultStatus<T>(true, object);
				}
			}
		}
		return new DefaultStatus<T>(false, object);
	}
}
