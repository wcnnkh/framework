package scw.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.exception.AlreadyExistsException;
import scw.common.exception.BeansException;

public abstract class AbstractBeanFactory implements BeanFactory {
	protected volatile Map<String, Object> singletonMap = new HashMap<String, Object>();
	protected volatile Map<String, Bean> beanMap = new HashMap<String, Bean>();
	protected Map<String, String> nameMappingMap = new HashMap<String, String>();

	// 注册一个单例
	public boolean registerSingleton(Class<?> type, Object bean) throws Exception {
		return registerSingleton(type.getName(), bean);
	}

	protected boolean registerSingleton(String name, Object bean) throws Exception {
		if (singletonMap.containsKey(name)) {
			return false;
		}

		singletonMap.put(name, bean);
		Bean beanInfo = getBean(name);
		if (beanInfo != null) {
			beanInfo.autowrite(bean);
			beanInfo.init(bean);
		}
		return true;
	}

	protected void putBean(String name, Bean bean) {
		if (contains(name)) {
			throw new AlreadyExistsException(name);
		}
		beanMap.put(name, bean);
	}

	public boolean registerNameMapping(String name, String mappingName) {
		if (nameMappingMap.containsKey(name)) {
			return false;
		}

		nameMappingMap.put(name, mappingName);
		return true;
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
			Object obj = singletonMap.get(bean.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(bean.getId());
					if (obj == null) {
						obj = bean.newInstance();
						singletonMap.put(bean.getId(), obj);
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

	private Bean getBeanCache(String name) {
		Bean bean = beanMap.get(name);
		if (bean == null) {
			String v = nameMappingMap.get(name);
			if (v != null) {
				bean = beanMap.get(v);
			}
		}
		return bean;
	}

	public Bean getBean(String name) {
		Bean bean = getBeanCache(name);
		if (bean == null) {
			synchronized (this) {
				bean = getBeanCache(name);
				if (bean == null) {
					try {
						bean = newBean(name);
						if (bean != null) {
							beanMap.put(bean.getId(), bean);
							if (bean.getNames() != null) {
								for (String n : bean.getNames()) {
									nameMappingMap.put(n, bean.getId());
								}
							}
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
		for (Entry<String, Object> entry : singletonMap.entrySet()) {
			Bean bean = getBean(entry.getKey());
			if (bean != null) {
				try {
					bean.destroy(entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		singletonMap.clear();
	}
}
