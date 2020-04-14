package scw.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import scw.core.instance.AbstractInstanceFactory;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.json.JSONUtils;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.MultiPropertyFactory;
import scw.util.value.property.PropertyFactory;

public abstract class AbstractBeanFactory extends
		AbstractInstanceFactory<BeanDefinition> implements BeanFactory, Init,
		Destroy {
	protected static Logger logger = LoggerUtils.getLogger(BeanFactory.class);
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

	@Override
	protected void afterCreation(long createTime, BeanDefinition definition,
			Object object) throws Exception {
		definition.init(object);
		if (logger.isTraceEnabled()) {
			logger.trace("create id [{}] instance [{}] use time:{}ms",
					definition.getId(), object.getClass().getName(),
					System.currentTimeMillis() - createTime);
		}
	}

	public void addPropertyFactory(PropertyFactory propertyFactory) {
		this.propertyFactory.add(propertyFactory);
	}

	public final BeanDefinition getDefinition(String name) {
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

	public final ValueWiredManager getValueWiredManager() {
		return valueWiredManager;
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

		Class<?> clz = ClassUtils.forNameNullable(n);
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
		for (Class<? extends Filter> clazz : InstanceUtils
				.getConfigurationClassList(Filter.class, propertyFactory)) {
			if (!isInstance(clazz)) {
				continue;
			}

			filterNames.add(clazz.getName());
		}

		propertyFactory.addAll(InstanceUtils.getConfigurationList(
				PropertyFactory.class, this, propertyFactory), true);
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
}
