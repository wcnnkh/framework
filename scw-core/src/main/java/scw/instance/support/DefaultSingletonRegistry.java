package scw.instance.support;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import scw.core.utils.StringUtils;
import scw.instance.SingletonRegistry;
import scw.util.CacheableSupplier;
import scw.util.Creator;
import scw.util.Result;
import scw.util.StaticSupplier;

public class DefaultSingletonRegistry implements SingletonRegistry{
	private volatile Map<String, Supplier<?>> singletionMap = new LinkedHashMap<String, Supplier<?>>();
	
	public void registerSingleton(String beanName, Object singletonObject) {
		registerSingleton(beanName, new StaticSupplier<Object>(singletonObject));
	}
	
	private void registerSingleton(String beanName, Supplier<?> supplier) {
		synchronized (singletionMap) {
			Supplier<?> old = singletionMap.get(beanName);
			if (old != null) {
				throw new IllegalStateException("Could not register object [" + supplier.get() +
						"] under bean name '" + beanName + "': there is already object [" + old.get() + "] bound");
			}
			
			singletionMap.put(beanName, supplier);
		}
	}

	public Object getSingleton(String beanName) {
		Supplier<?> instance = singletionMap.get(beanName);
		if(instance == null){
			return null;
		}
		return instance.get();
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
		Supplier<T> supplier = (Supplier<T>) singletionMap.get(beanName);
		if(supplier == null) {
			synchronized (singletionMap) {
				supplier = (Supplier<T>) singletionMap.get(beanName);
				if(supplier == null) {
					supplier = new CacheableSupplier<T>(creater.toSupplier());
					registerSingleton(beanName, supplier);
					return new Result<T>(true, supplier);
				}
			}
		}
		return new Result<T>(false, supplier);
		/*
		 * T object = (T) singletionMap.get(beanName); if(object == null){ synchronized
		 * (singletionMap) { object = (T) singletionMap.get(beanName); if(object ==
		 * null){ object = creater.create(); registerSingleton(beanName, object); return
		 * new Result<T>(true, object); } } } return new Result<T>(false, object);
		 */
	}
}
