package scw.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.property.PropertiesFactory;
import scw.common.exception.AlreadyExistsException;
import scw.common.exception.BeansException;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;

public abstract class AbstractBeanFactory implements BeanFactory {
	private volatile Map<String, Object> singletonMap = new HashMap<String, Object>();
	private volatile Map<String, Bean> beanMap = new HashMap<String, Bean>();
	private volatile Map<String, String> nameMappingMap = new HashMap<String, String>();
	private boolean init = false;

	public void registerNameMapping(String key, String value) {
		if (nameMappingMap.containsKey(key)) {
			throw new AlreadyExistsException(key);
		}

		synchronized (nameMappingMap) {
			nameMappingMap.put(key, value);
		}
	}

	public void addSingleton(String id, Object singleton) {
		Bean bean = getBean(id);
		if (bean == null) {
			throw new scw.common.exception.NotFoundException(id);
		}

		synchronized (singletonMap) {
			singletonMap.put(id, singleton);
			try {
				bean.autowrite(singleton);
				bean.init(singleton);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addBeanConfigFactory(BeanConfigFactory beanConfigFactory) {
		if (beanConfigFactory != null) {
			Map<String, Bean> map = beanConfigFactory.getBeanMap();
			if (map != null) {
				synchronized (beanMap) {
					for (Entry<String, Bean> entry : map.entrySet()) {
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
	public <T> T get(String name) {
		if (!init) {
			throw new BeansException("还未初始化");
		}

		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		Bean bean = getBean(name);
		if (bean == null) {
			return null;
		}

		if (bean.isSingleton()) {
			obj = singletonMap.get(bean.getId());
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
							throw new BeansException(bean.getId(), e);
						}
					}
				}
			}
			return (T) obj;
		} else {
			obj = bean.newInstance();
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
		Bean bean = getBeanCache(name);
		if (bean == null) {
			synchronized (beanMap) {
				bean = getBeanCache(name);
				if (bean == null) {
					try {
						bean = newBean(name);
						if (bean != null) {
							beanMap.put(bean.getId(), bean);
							addBeanNameMapping(bean);
						}
					} catch (Exception e) {
						throw new BeansException(e);
					}
				}
			}
		}
		return bean;
	}

	private void addBeanNameMapping(Bean bean) {
		if (bean.getNames() != null) {
			synchronized (bean) {
				for (String n : bean.getNames()) {
					nameMappingMap.put(n, bean.getId());
				}
			}
		}
	}

	public boolean contains(String name) {
		boolean b = singletonMap.containsKey(name) || nameMappingMap.containsKey(name) || beanMap.containsKey(name);
		if (b) {
			return b;
		}

		try {
			Class<?> clz = Class.forName(name);
			if (ClassUtils.isInstance(clz)) {
				b = true;
			}
		} catch (ClassNotFoundException e) {
		}
		return false;
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

	private Bean newBean(String name) {
		try {
			String n = nameMappingMap.get(name);
			if (n == null) {
				n = name;
			}
			Class<?> clz = Class.forName(n);
			if (!ClassUtils.isInstance(clz)) {
				return null;
			}
			return new AnnotationBean(this, getPropertiesFactory(), clz, getFilterNames());
		} catch (Exception e) {
		}
		return null;
	}

	public abstract PropertiesFactory getPropertiesFactory();

	public abstract String getPackages();

	public abstract String[] getFilterNames();

	public Collection<Class<?>> getClassList() {
		return ClassUtils.getClasses(getPackages());
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
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public synchronized void destroy() {
		if (!init) {
			throw new BeansException("还未初始化");
		}
		init = false;
		
		try {
			if (isInitStatic()) {
				BeanUtils.destroyStaticMethod(getClassList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Entry<String, Object> entry : singletonMap.entrySet()) {
			Bean bean = getBean(entry.getKey());
			try {
				bean.destroy(entry.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
