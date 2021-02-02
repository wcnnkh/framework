package scw.instance.support;

import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.instance.SingletonRegistry;
import scw.util.Creator;
import scw.util.Result;

public class DefaultSingletonRegistry implements SingletonRegistry{
	private volatile Map<String, Object> singletionMap = new LinkedHashMap<String, Object>();
	
	public void registerSingleton(String beanName, Object singletonObject) {
		synchronized (singletionMap) {
			Object oldObject = singletionMap.get(beanName);
			if (oldObject != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject +
						"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			}
			
			addSingleton(beanName, singletonObject);
		}
	}

	public Object getSingleton(String beanName) {
		Object instance = singletionMap.get(beanName);
		if(instance == null){
			return null;
		}
		return instance;
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
	public <T> Result<T> getSingleton(final String beanName,
			Creator<T> creater) {
		T object = (T) singletionMap.get(beanName);
		if(object == null){
			synchronized (singletionMap) {
				object = (T) singletionMap.get(beanName);
				if(object == null){
					object = creater.create();
					addSingleton(beanName, object);
					return new Result<T>(true, object);
				}
			}
		}
		return new Result<T>(false, object);
	}
	
	protected void addSingleton(String beanName, Object singleton){
		synchronized (singletionMap) {
			if(singletionMap.containsKey(beanName)){
				throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
			}
			singletionMap.put(beanName, singleton);
		}
	}
}
