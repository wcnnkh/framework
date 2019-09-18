package scw.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Proxy;
import scw.beans.auto.AutoBean;
import scw.beans.auto.AutoBeanDefinition;
import scw.beans.auto.AutoBeanService;
import scw.beans.auto.AutoBeanUtils;
import scw.beans.property.ValueWiredManager;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.exception.BeansException;
import scw.core.exception.NestedRuntimeException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.ResourceUtils;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanFactory implements BeanFactory, Init, Destroy {
	protected Logger logger = LoggerUtils.getLogger(getClass());
	private volatile LinkedHashMap<String, Object> singletonMap = new LinkedHashMap<String, Object>();
	private volatile Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	private volatile Map<String, String> nameMappingMap = new HashMap<String, String>();
	private LinkedList<Destroy> destroys = new LinkedList<Destroy>();
	private volatile HashSet<String> notFoundSet = new HashSet<String>();

	protected boolean isEnableNotFoundSet() {
		return true;
	}

	public void registerNameMapping(String key, String value) {
		if (nameMappingMap.containsKey(key)) {
			throw new AlreadyExistsException(key);
		}

		synchronized (nameMappingMap) {
			nameMappingMap.put(key, value);
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
							logger.warn("Already exist id:{}, definition:{}", key,
									JSONUtils.toJSONString(entry.getValue()));
							continue;
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
							logger.warn("Already exist name:{}, definition:{}", key,
									JSONUtils.toJSONString(entry.getValue()));
							continue;
						}
						nameMappingMap.put(key, entry.getValue());
					}
				}
			}

			Collection<Destroy> destroys = beanConfigFactory.getDestroys();
			if (destroys != null) {
				destroys.addAll(destroys);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object... params) {

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

	/**
	 * 对静态类型的注解扫描目录
	 * 
	 * @return
	 */
	protected abstract String getInitStaticPackage();

	public final BeanDefinition getBeanDefinition(String name) {
		if (isEnableNotFoundSet()) {
			if (notFoundSet.contains(name)) {
				return null;
			}
		}

		BeanDefinition beanDefinition = getBeanCache(name);
		if (beanDefinition == null) {
			synchronized (this) {
				beanDefinition = getBeanCache(name);
				if (beanDefinition == null) {
					beanDefinition = newBeanDefinition(name);
				}
			}

			if (beanDefinition != null) {
				synchronized (beanMap) {
					beanMap.put(beanDefinition.getId(), beanDefinition);
				}
				addBeanNameMapping(beanDefinition);
			}
		}

		if (beanDefinition == null) {
			if (isEnableNotFoundSet()) {
				if (!notFoundSet.contains(name)) {
					synchronized (notFoundSet) {
						notFoundSet.add(name);
					}
				}
			}
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

	public final boolean contains(String name) {
		boolean b = singletonMap.containsKey(name) || nameMappingMap.containsKey(name) || beanMap.containsKey(name);
		if (b) {
			return b;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		return beanDefinition != null;
	}

	public boolean isInstance(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		if (beanDefinition == null) {
			return false;
		}

		return singletonMap.containsKey(beanDefinition.getId()) || beanDefinition.isInstance();
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
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

	public abstract ValueWiredManager getValueWiredManager();

	private BeanDefinition newBeanDefinition(String name) {
		String n = nameMappingMap.get(name);
		if (n == null) {
			n = name;
		}

		Class<?> clz = null;
		try {
			clz = Class.forName(n, false, ClassUtils.getDefaultClassLoader());
		} catch (Throwable e) {
		}

		if (clz == null) {
			return null;
		}

		Proxy proxy = clz.getAnnotation(Proxy.class);
		if (proxy != null) {
			return new CommonBeanDefinition(getValueWiredManager(), this, getPropertyFactory(), clz);
		}

		AutoImpl autoConfig = clz.getAnnotation(AutoImpl.class);
		if (autoConfig == null || AutoBeanService.class.isAssignableFrom(clz)) {
			if (ReflectUtils.isInstance(clz)) {
				try {
					return new CommonBeanDefinition(getValueWiredManager(), this, getPropertyFactory(), clz);
				} catch (Exception e) {
					throw new BeansException(clz.getName(), e);
				}
			}
		}

		if (AutoBeanService.class.isAssignableFrom(clz)) {
			return null;
		}

		AutoBean autoBean = AutoBeanUtils.autoBeanService(clz, autoConfig, this, getPropertyFactory());
		if (autoBean != null) {
			try {
				return new AutoBeanDefinition(getValueWiredManager(), this, getPropertyFactory(), clz, autoBean);
			} catch (Exception e) {
				throw new BeansException(clz.getName(), e);
			}
		}
		return null;
	}

	public abstract PropertyFactory getPropertyFactory();

	public synchronized void init() {
		try {
			BeanUtils.initStatic(getValueWiredManager(), this, getPropertyFactory(),
					ResourceUtils.getClassList(getInitStaticPackage()));
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}

	public synchronized void destroy() {
		try {
			BeanUtils.destroyStaticMethod(getValueWiredManager(), ResourceUtils.getClassList(getInitStaticPackage()));
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

		ListIterator<Destroy> iterator = destroys.listIterator(destroys.size());
		while (iterator.hasPrevious()) {
			iterator.previous().destroy();
		}
	}

	public <T> T getInstance(Class<T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	public <T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object... params) {
		return getInstance(type.getName(), parameterTypes, params);
	}
}
