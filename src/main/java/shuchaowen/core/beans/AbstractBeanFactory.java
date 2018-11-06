package shuchaowen.core.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.exception.BeansException;

public abstract class AbstractBeanFactory implements BeanFactory {
	protected volatile Map<String, Object> singletonMap = new HashMap<String, Object>();
	protected volatile Map<String, Bean> beanMap = new HashMap<String, Bean>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();

	// 注册一个单例
	public void registerSingleton(Class<?> type, Object bean) {
		registerSingleton(type.getName(), bean);
	}

	public void registerSingleton(String name, Object bean) {
		if (singletonMap.containsKey(name)) {
			throw new AlreadyExistsException(name);
		}

		singletonMap.put(name, bean);
	}
	
	public void putBean(String name, Bean bean) {
		if (contains(name)) {
			throw new AlreadyExistsException(name);
		}
		beanMap.put(name, bean);
	}

	public void putNameMapping(String name, String mappingName) {
		if (nameMappingMap.containsKey(name)) {
			throw new AlreadyExistsException(name);
		}
		nameMappingMap.put(name, mappingName);
	}

	public boolean contains(String name) {
		return singletonMap.containsKey(name) || nameMappingMap.containsKey(name) || beanMap.containsKey(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		Bean bean = getBean(name);
		if (bean == null) {
			return null;
		}

		if (bean.isSingleton()) {
			Object obj = singletonMap.get(name);
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(name);
					if (obj == null) {
						obj = bean.newInstance();
						singletonMap.put(name, obj);
						try {
							bean.autowrite(obj);
							bean.init(obj);
						} catch (Exception e) {
							throw new BeansException(e);
						}
					}
				}
			}
			return (T) obj;
		} else {
			Object obj = bean.newInstance();
			try {
				bean.autowrite(obj);
				bean.init(obj);
			} catch (Exception e) {
				throw new BeansException(e);
			}
			return (T) obj;
		}
	}

	public <T> T get(Class<T> type) {
		return get(type.getName());
	}

	public Bean getBean(String name) {
		String v = nameMappingMap.get(name);
		v = v == null ? name : v;
		Bean bean = beanMap.get(v);
		if (bean == null) {
			synchronized (beanMap) {
				bean = beanMap.get(v);
				if (bean == null) {
					try {
						bean = newBean(v);
						if (bean != null) {
							beanMap.put(v, bean);
						}
					} catch (Exception e) {
						throw new BeansException(e);
					}
				}
			}
		}
		return bean;
	}

	protected abstract Bean newBean(String name) throws Exception;

	public void destroy() {
		HashSet<Class<?>> tagSet = new HashSet<Class<?>>();
		for (Entry<String, Object> entry : singletonMap.entrySet()) {
			if (contains(entry.getKey())) {
				if (tagSet.contains(entry.getValue().getClass())) {
					continue;
				}

				try {
					Bean bean = getBean(entry.getKey());
					if (bean != null) {
						tagSet.add(entry.getValue().getClass());
						bean.destroy(entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		singletonMap.clear();
	}
}
