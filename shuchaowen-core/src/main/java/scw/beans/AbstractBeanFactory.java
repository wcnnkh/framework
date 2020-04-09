package scw.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.aop.Filter;
import scw.beans.annotation.AutoImpl;
import scw.beans.auto.AutoBean;
import scw.beans.auto.AutoBeanDefinition;
import scw.beans.auto.AutoBeanUtils;
import scw.beans.property.ValueWiredManager;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.instance.InstanceFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.json.JSONUtils;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.MultiPropertyFactory;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanFactory implements BeanFactory, Init, Destroy {
	protected static Logger logger = LoggerUtils.getLogger(BeanFactory.class);
	private volatile LinkedHashMap<String, Object> singletonMap = new LinkedHashMap<String, Object>();
	private volatile Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	private volatile Map<String, String> nameMappingMap = new HashMap<String, String>();
	private final LinkedList<Destroy> destroys = new LinkedList<Destroy>();
	private final LinkedList<Init> inits = new LinkedList<Init>();
	private volatile HashSet<String> notFoundSet = new HashSet<String>();
	protected final MultiPropertyFactory propertyFactory = new MultiPropertyFactory();
	private final LinkedHashSet<String> filterNames = new LinkedHashSet<String>();
	private final ValueWiredManager valueWiredManager;

	public AbstractBeanFactory() {
		this.valueWiredManager = new ValueWiredManager(propertyFactory, this);
		appendSingleByPropertyFactory();
		appendSingleByBeanFactory();
	}

	private void appendSingleByPropertyFactory() {
		singletonMap.put(PropertyFactory.class.getName(), propertyFactory);
		beanMap.put(PropertyFactory.class.getName(), new EmptyBeanDefinition(
				PropertyFactory.class, propertyFactory, null, false));
	}

	private void appendSingleByBeanFactory() {
		singletonMap.put(BeanFactory.class.getName(), this);
		beanMap.put(
				BeanFactory.class.getName(),
				new EmptyBeanDefinition(BeanFactory.class, this,
						new String[] { InstanceFactory.class.getName() }, false));
		nameMappingMap.put(InstanceFactory.class.getName(),
				BeanFactory.class.getName());
	}

	protected final synchronized void addFilterName(Collection<String> names) {
		if (CollectionUtils.isEmpty(names)) {
			return;
		}

		filterNames.addAll(names);
	}

	public final Collection<String> getFilterNames() {
		return Collections.unmodifiableCollection(filterNames);
	}

	public final PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	protected boolean isEnableNotFoundSet() {
		return propertyFactory.getValue("beans.notfound", boolean.class, true);
	}

	protected final void addBeanConfiguration(
			BeanConfiguration beanConfiguration) {
		if (beanConfiguration == null) {
			return;
		}

		Map<String, BeanDefinition> map = beanConfiguration.getBeanMap();
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

		Map<String, String> nameMapping = beanConfiguration.getNameMappingMap();
		if (nameMapping != null) {
			synchronized (nameMappingMap) {
				for (Entry<String, String> entry : nameMapping.entrySet()) {
					String key = entry.getKey();
					if (nameMappingMap.containsKey(key)) {
						logger.warn("Already exist name:{}, definition:{}",
								key, JSONUtils.toJSONString(entry.getValue()));
						continue;
					}
					nameMappingMap.put(key, entry.getValue());
				}
			}
		}

		destroys.addAll(beanConfiguration.getDestroys());
		inits.addAll(beanConfiguration.getInits());
	}
	
	private Object createInstance(BeanDefinition beanDefinition, Class<?>[] parameterTypes,
			Object... params){
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = beanDefinition.create(parameterTypes, params);
			if(beanDefinition.isInstance()){
				singletonMap.put(beanDefinition.getId(), obj);
			}
			beanDefinition.init(obj);
			loggerCreateInstanceTime(t, beanDefinition, obj);
		} catch (Exception e) {
			throw new BeansException(beanDefinition.getId(), e);
		}
		return obj;
	}
	
	private void loggerCreateInstanceTime(long createTime, BeanDefinition beanDefinition, Object instance){
		if(logger.isTraceEnabled()){
			logger.trace("create id [{}] instance [{}] use time:{}ms",
					beanDefinition.getId(), instance.getClass().getName(),
					System.currentTimeMillis() - createTime);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object... params) {
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
						obj = createInstance(beanDefinition, parameterTypes, params);
					}
				}
			}
		} else {
			obj = createInstance(beanDefinition, parameterTypes, params);
		}
		return (T) obj;
	}

	private Object createInstance(BeanDefinition beanDefinition,
			Object... params) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = beanDefinition.create(params);
			if (beanDefinition.isInstance()) {
				singletonMap.put(beanDefinition.getId(), obj);
			}
			beanDefinition.init(obj);
			loggerCreateInstanceTime(t, beanDefinition, obj);
		} catch (Exception e) {
			throw new BeansException(beanDefinition.getId(), e);
		}
		return obj;
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
						obj = createInstance(beanDefinition, params);
					}
				}
			}
		} else {
			obj = createInstance(beanDefinition, params);
		}
		return (T) obj;
	}

	private Object createInstance(BeanDefinition beanDefinition) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = beanDefinition.create();
			if (beanDefinition.isSingleton()) {
				singletonMap.put(beanDefinition.getId(), obj);
			}
			beanDefinition.init(obj);
			loggerCreateInstanceTime(t, beanDefinition, obj);
		} catch (Exception e) {
			throw new BeansException(beanDefinition.getId(), e);
		}
		return obj;
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
						obj = createInstance(beanDefinition);
					}
				}
			}
		} else {
			obj = createInstance(beanDefinition);
		}
		return (T) obj;
	}

	public <T> T getInstance(Class<? extends T> type) {
		return getInstance(type.getName());
	}

	public void addPropertyFactory(PropertyFactory propertyFactory) {
		this.propertyFactory.add(propertyFactory);
	}

	public final BeanDefinition getBeanDefinition(String name) {
		BeanDefinition beanDefinition = getBeanCache(name);
		if (beanDefinition == null) {
			if (isEnableNotFoundSet()) {
				if (notFoundSet.contains(name)) {
					return null;
				}
			}

			synchronized (this) {
				beanDefinition = getBeanCache(name);
				if (beanDefinition == null) {
					long t = System.currentTimeMillis();
					beanDefinition = newBeanDefinition(name);
					if (beanDefinition != null) {
						t = System.currentTimeMillis() - t;
						if (logger.isDebugEnabled()) {
							logger.debug(
									"create [{}] definition isInstance={} isSingletion={} use time {} ms",
									name, beanDefinition.isInstance(),
									beanDefinition.isSingleton(), t);
						}
					}
				}
			}

			if (beanDefinition != null) {
				synchronized (beanMap) {
					beanMap.put(beanDefinition.getId(), beanDefinition);
				}
				addBeanNameMapping(beanDefinition.getNames(),
						beanDefinition.getId());
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

	protected void addBeanNameMapping(String[] names, String id) {
		if (names != null) {
			synchronized (nameMappingMap) {
				for (String n : names) {
					if (nameMappingMap.containsKey(n)) {
						throw new AlreadyExistsException("存在相同的名称映射:" + n
								+ ", oldId=" + nameMappingMap.get(n)
								+ ",newId=" + id);
					}
					nameMappingMap.put(n, id);
				}
			}
		}
	}

	public boolean isSingleton(Class<?> clazz) {
		return isSingleton(clazz.getName());
	}

	public boolean isSingleton(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		if (beanDefinition == null) {
			return false;
		}

		return beanDefinition.isSingleton();
	}

	public final boolean contains(String name) {
		boolean b = singletonMap.containsKey(name)
				|| nameMappingMap.containsKey(name)
				|| beanMap.containsKey(name);
		if (b) {
			return b;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		return beanDefinition != null;
	}

	public final ValueWiredManager getValueWiredManager() {
		return valueWiredManager;
	}

	public final boolean isInstance(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition beanDefinition = getBeanDefinition(name);
		if (beanDefinition == null) {
			return false;
		}

		return singletonMap.containsKey(beanDefinition.getId())
				|| beanDefinition.isInstance();
	}

	public final boolean isInstance(Class<?> clazz) {
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

	private BeanDefinition newBeanDefinition(String name) {
		String n = nameMappingMap.get(name);
		if (n == null) {
			n = name;
		}

		Class<?> clz = null;
		try {
			clz = ClassUtils.forName(n);
		} catch (Throwable e) {
		}

		if (clz == null) {
			return null;
		}

		long t = System.currentTimeMillis();
		AutoBean autoBean = AutoBeanUtils.autoBeanService(clz,
				clz.getAnnotation(AutoImpl.class), this, getPropertyFactory());
		if (autoBean != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("find [{}] use time {}ms", clz,
						System.currentTimeMillis() - t);
			}
			try {
				return new AutoBeanDefinition(valueWiredManager, this,
						getPropertyFactory(), clz, autoBean);
			} catch (Exception e) {
				throw new BeansException(clz.getName(), e);
			}
		}
		return null;
	}

	public synchronized void init() {
		for (Class<? extends Filter> clazz : BeanUtils
				.getConfigurationClassList(Filter.class)) {
			if (!isInstance(clazz)) {
				continue;
			}

			filterNames.add(clazz.getName());
		}
		
		propertyFactory.addAll(BeanUtils.getConfigurationList(PropertyFactory.class, this), true);
		for (Init init : inits) {
			init.init();
		}
		inits.clear();
	}

	public synchronized void destroy() {
		valueWiredManager.destroy();

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

	public <T> T getInstance(Class<? extends T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	public <T> T getInstance(Class<? extends T> type, Class<?>[] parameterTypes,
			Object... params) {
		return getInstance(type.getName(), parameterTypes, params);
	}
}
