package scw.beans;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.annotation.InterfaceProxy;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.PropertiesFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.exception.BeansException;
import scw.core.exception.NestedRuntimeException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ResourceUtils;

public abstract class AbstractBeanFactory implements BeanFactory, Init, Destroy {
	private volatile LinkedHashMap<String, Object> singletonMap = new LinkedHashMap<String, Object>();
	private volatile Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	private volatile Map<String, String> nameMappingMap = new HashMap<String, String>();
	private volatile Map<String, Object> notFoundMap = new HashMap<String, Object>();

	private boolean init = false;

	public void registerNameMapping(String key, String value) {
		if (nameMappingMap.containsKey(key)) {
			throw new AlreadyExistsException(key);
		}

		synchronized (nameMappingMap) {
			nameMappingMap.put(key, value);
		}
	}

	private void addNotFoundName(String name) {
		if (!notFoundMap.containsKey(name)) {
			synchronized (notFoundMap) {
				if (!notFoundMap.containsKey(name)) {
					notFoundMap.put(name, null);
				}
			}
		}
	}

	public void addSingleton(String id, Object singleton) {
		if (singletonMap.containsKey(id)) {
			throw new AlreadyExistsException(id);
		}

		synchronized (singletonMap) {
			singletonMap.put(id, singleton);
		}
	}

	public void addBeanConfigFactory(BeanConfigFactory beanConfigFactory) {
		if (beanConfigFactory != null) {
			Map<String, BeanDefinition> map = beanConfigFactory.getBeanMap();
			if (map != null) {
				synchronized (beanMap) {
					for (Entry<String, BeanDefinition> entry : map.entrySet()) {
						String key = entry.getKey();
						if (beanMap.containsKey(key)) {
							throw new AlreadyExistsException(key);
						}

						beanMap.put(key, entry.getValue());
					}
				}
			}

			Map<String, String> nameMapping = beanConfigFactory.getNameMappingMap();
			if (nameMapping != null) {
				synchronized (nameMappingMap) {
					for (Entry<String, String> entry : nameMapping.entrySet()) {
						String key = entry.getKey();
						if (nameMappingMap.containsKey(key)) {
							throw new AlreadyExistsException(key);
						}
						nameMappingMap.put(key, entry.getValue());
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		if (!init) {
			throw new BeansException("还未初始化");
		}

		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		if (beanDefinition.isSingleton()) {
			obj = singletonMap.get(beanDefinition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(beanDefinition.getId());
					if (obj == null) {
						obj = beanDefinition.create(parameterTypes, params);
						singletonMap.put(beanDefinition.getId(), obj);
						try {
							beanDefinition.autowrite(obj);
							beanDefinition.init(obj);
						} catch (Exception e) {
							throw new BeansException(beanDefinition.getId(), e);
						}
					}
				}
			}
			return (T) obj;
		} else {
			obj = beanDefinition.create(parameterTypes, params);
			try {
				beanDefinition.autowrite(obj);
				beanDefinition.init(obj);
			} catch (Exception e) {
				throw new BeansException(e);
			}
			return (T) obj;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Object... params) {
		if (!init) {
			throw new BeansException("还未初始化");
		}

		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		if (beanDefinition.isSingleton()) {
			obj = singletonMap.get(beanDefinition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(beanDefinition.getId());
					if (obj == null) {
						obj = beanDefinition.create(params);
						singletonMap.put(beanDefinition.getId(), obj);
						try {
							beanDefinition.autowrite(obj);
							beanDefinition.init(obj);
						} catch (Exception e) {
							throw new BeansException(beanDefinition.getId(), e);
						}
					}
				}
			}
			return (T) obj;
		} else {
			obj = beanDefinition.create(params);
			try {
				beanDefinition.autowrite(obj);
				beanDefinition.init(obj);
			} catch (Exception e) {
				throw new BeansException(e);
			}
			return (T) obj;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		if (!init) {
			throw new BeansException("还未初始化");
		}

		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		if (beanDefinition == null) {
			return null;
		}

		if (beanDefinition.isSingleton()) {
			obj = singletonMap.get(beanDefinition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(beanDefinition.getId());
					if (obj == null) {
						obj = beanDefinition.create();
						singletonMap.put(beanDefinition.getId(), obj);
						try {
							beanDefinition.autowrite(obj);
							beanDefinition.init(obj);
						} catch (Exception e) {
							throw new BeansException(beanDefinition.getId(), e);
						}
					}
				}
			}
			return (T) obj;
		} else {
			obj = beanDefinition.create();
			try {
				beanDefinition.autowrite(obj);
				beanDefinition.init(obj);
			} catch (Exception e) {
				throw new BeansException(e);
			}
			return (T) obj;
		}
	}

	public <T> T getInstance(Class<T> type) {
		return getInstance(type.getName());
	}

	public BeanDefinition getBeanDefinition(String name) {
		if (notFoundMap.containsKey(name)) {
			return null;
		}

		BeanDefinition beanDefinition = getBeanCache(name);
		if (beanDefinition == null) {
			synchronized (beanMap) {
				beanDefinition = getBeanCache(name);
				if (beanDefinition == null) {
					try {
						beanDefinition = newBeanDefinition(name);
						if (beanDefinition != null) {
							beanMap.put(beanDefinition.getId(), beanDefinition);
							addBeanNameMapping(beanDefinition);
						}
					} catch (Exception e) {
						throw new BeansException(e);
					}
				}
			}
		}

		if (beanDefinition == null) {
			addNotFoundName(name);
		}
		return beanDefinition;
	}

	private void addBeanNameMapping(BeanDefinition beanDefinition) {
		if (beanDefinition.getNames() != null) {
			synchronized (beanDefinition) {
				for (String n : beanDefinition.getNames()) {
					nameMappingMap.put(n, beanDefinition.getId());
				}
			}
		}
	}

	public boolean contains(String name) {
		boolean b = singletonMap.containsKey(name) || nameMappingMap.containsKey(name) || beanMap.containsKey(name);
		if (b) {
			return b;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		return beanDefinition != null;
	}

	private BeanDefinition getBeanCache(String name) {
		BeanDefinition beanDefinition = beanMap.get(name);
		if (beanDefinition == null) {
			String v = nameMappingMap.get(name);
			if (v != null) {
				beanDefinition = beanMap.get(v);
			}
		}
		return beanDefinition;
	}

	private BeanDefinition newBeanDefinition(String name) {
		try {
			String n = nameMappingMap.get(name);
			if (n == null) {
				n = name;
			}

			Class<?> clz = Class.forName(n);
			if(clz.isInterface()){
				InterfaceProxy interfaceProxy = clz.getAnnotation(InterfaceProxy.class);
				if (interfaceProxy != null) {
					InvocationHandler invocationHandler = getInstance(interfaceProxy.value());
					return new InterfaceProxyBeanDefinition(this, clz, invocationHandler, getFilterNames());
				}
			}

			if (!ReflectUtils.isInstance(clz)) {
				return null;
			}
			return new AnnotationBeanDefinition(this, getPropertiesFactory(), clz, getFilterNames());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract PropertiesFactory getPropertiesFactory();

	public abstract String getPackages();

	public abstract String[] getFilterNames();

	public Collection<Class<?>> getClassList() {
		return ResourceUtils.getClassList(getPackages());
	}

	/**
	 * 是否初始化静态方法,兼容老版本
	 * 
	 * @return
	 */
	public abstract boolean isInitStatic();

	public synchronized void init() {
		if (init) {
			throw new BeansException("已经初始化了");
		}
		init = true;

		try {
			if (isInitStatic()) {
				BeanUtils.initStatic(this, getPropertiesFactory(), getClassList());
			}
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}

	public synchronized void destroy() {
		if (!init) {
			throw new BeansException("还未初始化");
		}

		try {
			if (isInitStatic()) {
				BeanUtils.destroyStaticMethod(getClassList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		synchronized (singletonMap) {
			List<String> beanKeyList = new ArrayList<String>();
			for (Entry<String, Object> entry : singletonMap.entrySet()) {
				beanKeyList.add(entry.getKey());
			}

			for (String id : beanKeyList) {
				BeanDefinition beanDefinition = getBeanCache(id);
				if (beanDefinition == null) {
					continue;
				}

				Object obj = singletonMap.get(id);
				try {
					beanDefinition.destroy(obj);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		init = false;
	}

	public <T> T getInstance(Class<T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object... params) {
		return getInstance(type.getName(), parameterTypes, params);
	}
}
