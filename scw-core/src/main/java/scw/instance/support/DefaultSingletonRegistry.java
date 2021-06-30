package scw.instance.support;

import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.instance.SingletonRegistry;
import scw.util.Creator;
import scw.util.Result;

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
	public <T, E extends Throwable> Result<T> getSingleton(String name, Creator<T, E> creater) throws E {
		T object = (T) singletionMap.get(name);
		if (object == null) {
			synchronized (singletionMap) {
				object = (T) singletionMap.get(name);
				if (object == null) {
					object = creater.create();
					registerSingleton(name, object);
					return new Result<T>(true, object);
				}
			}
		}
		return new Result<T>(false, object);
	}
}
