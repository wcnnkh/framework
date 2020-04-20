package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
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
import scw.beans.auto.AutoBeanUtils;
import scw.beans.definition.BeanDefinition;
import scw.beans.definition.DefaultBeanDefinition;
import scw.beans.definition.builder.BeanBuilder;
import scw.beans.method.MethodBeanConfiguration;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.instance.InstanceException;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.json.JSONUtils;
import scw.lang.AlreadyExistsException;
import scw.lang.Ignore;
import scw.lang.UnsupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.MultiPropertyFactory;
import scw.util.value.property.PropertyFactory;

public abstract class DefaultBeanFactory implements BeanFactory, Init, Destroy {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	protected volatile LinkedHashMap<String, Object> singletonMap = new LinkedHashMap<String, Object>();
	private volatile Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	private volatile Map<String, String> nameMappingMap = new HashMap<String, String>();
	private volatile HashSet<String> notFoundSet = new HashSet<String>();
	protected final MultiPropertyFactory propertyFactory = new MultiPropertyFactory();
	private final LinkedHashSet<String> filterNames = new LinkedHashSet<String>();
	private final LinkedList<BeanFactoryLifeCycle> beanFactoryLifeCycles = new LinkedList<BeanFactoryLifeCycle>();

	public DefaultBeanFactory() {
		singletonMap.put(BeanFactory.class.getName(), this);
	}

	protected BeanDefinition getDefinitionByCache(String name) {
		BeanDefinition beanDefinition = beanMap.get(name);
		if (beanDefinition == null) {
			String v = nameMappingMap.get(name);
			if (v != null) {
				beanDefinition = beanMap.get(v);
			}
		}
		return beanDefinition;
	}

	public final BeanDefinition getDefinition(String name) {
		BeanDefinition beanDefinition = getDefinitionByCache(name);
		if (beanDefinition == null) {
			if (notFoundSet.contains(name)) {
				return null;
			}

			synchronized (beanMap) {
				synchronized (nameMappingMap) {
					beanDefinition = getDefinitionByCache(name);
					if (beanDefinition == null) {
						beanDefinition = autoCreateBeanDefinition(name);
						if (beanDefinition != null) {
							addBeanDefinition(beanDefinition, true);
						}
					}
				}
			}
		}

		if (beanDefinition == null) {
			if (!notFoundSet.contains(name)) {
				synchronized (notFoundSet) {
					notFoundSet.add(name);
				}
			}
		}
		return beanDefinition;
	}

	protected BeanDefinition autoCreateBeanDefinition(String name) {
		String n = nameMappingMap.get(name);
		if (n == null) {
			n = name;
		}

		Class<?> clz = ClassUtils.forNameNullable(n);
		if (clz == null) {
			return null;
		}

		AutoImpl autoImpl = clz.getAnnotation(AutoImpl.class);
		if (autoImpl == null) {
			if (clz.getAnnotation(Ignore.class) != null) {
				return null;
			}
		}

		BeanBuilder autoBean = AutoBeanUtils.autoBeanService(clz, autoImpl,
				this, getPropertyFactory());
		if (autoBean != null) {
			return new DefaultBeanDefinition(this, propertyFactory, clz,
					autoBean);
		}
		return null;
	}

	public BeanDefinition getDefinition(Class<?> clazz) {
		return getDefinition(clazz.getName());
	}

	public boolean isSingleton(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return definition.isSingleton();
	}

	public boolean isSingleton(Class<?> clazz) {
		return isSingleton(clazz.getName());
	}

	public <T> T getInstance(Class<? extends T> clazz) {
		return getInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		Object object = singletonMap.get(name);
		if (object != null) {
			return (T) object;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			object = singletonMap.get(definition.getId());
			if (object == null) {
				synchronized (singletonMap) {
					object = singletonMap.get(definition.getId());
					if (object == null) {
						object = createInternal(definition);
					}
				}
			}
		} else {
			object = createInternal(definition);
		}
		return (T) object;
	}

	protected void init(long createTime, BeanDefinition definition,
			Object object) throws Exception {
		if (definition.isSingleton()) {
			singletonMap.put(definition.getId(), object);
		}
		definition.init(object);
	}

	public MultiPropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	private Object createInternal(BeanDefinition definition) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create();
			init(t, definition, obj);
		} catch (Exception e) {
			throw new InstanceException(definition.getId(), e);
		}
		return obj;
	}

	public boolean isInstance(String name) {
		if (singletonMap.containsKey(name)) {
			return true;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return false;
		}

		return singletonMap.containsKey(definition.getId())
				|| definition.isInstance();
	}

	public boolean isInstance(Class<?> clazz) {
		return isInstance(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Object... params) {
		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			obj = singletonMap.get(definition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(definition.getId());
					if (obj == null) {
						obj = createInternal(definition, params);
					}
				}
			}
		} else {
			obj = createInternal(definition, params);
		}
		return (T) obj;
	}

	private Object createInternal(BeanDefinition definition, Object... params) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create(params);
			init(t, definition, obj);
		} catch (Exception e) {
			throw new InstanceException(definition.getId(), e);
		}
		return obj;
	}

	public <T> T getInstance(Class<? extends T> type, Object... params) {
		return getInstance(type.getName(), params);
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object... params) {
		Object obj = singletonMap.get(name);
		if (obj != null) {
			return (T) obj;
		}

		BeanDefinition definition = getDefinition(name);
		if (definition == null) {
			return null;
		}

		if (definition.isSingleton()) {
			obj = singletonMap.get(definition.getId());
			if (obj == null) {
				synchronized (singletonMap) {
					obj = singletonMap.get(definition.getId());
					if (obj == null) {
						obj = createInternal(definition, parameterTypes, params);
					}
				}
			}
		} else {
			obj = createInternal(definition, parameterTypes, params);
		}
		return (T) obj;
	}

	private Object createInternal(BeanDefinition definition,
			Class<?>[] parameterTypes, Object... params) {
		long t = System.currentTimeMillis();
		Object obj;
		try {
			obj = definition.create(parameterTypes, params);
			init(t, definition, obj);
		} catch (Exception e) {
			throw new BeansException(definition.getId(), e);
		}
		return obj;
	}

	public <T> T getInstance(Class<? extends T> type,
			Class<?>[] parameterTypes, Object... params) {
		return getInstance(type.getName(), parameterTypes, params);
	}

	protected void addBeanDefinition(BeanDefinition beanDefinition,
			boolean throwExistError) {
		BeanDefinition definition = getDefinitionByCache(beanDefinition.getId());
		if (definition != null) {
			logger.warn("Already exist id:{}, definition:{}",
					beanDefinition.getId(),
					JSONUtils.toJSONString(beanDefinition));
			if (throwExistError) {
				throw new AlreadyExistsException("存在相同ID的映射:"
						+ JSONUtils.toJSONString(beanDefinition));
			}

			return;
		}

		if (addBeanNameMapping(beanDefinition.getNames(),
				beanDefinition.getId(), throwExistError)) {
			beanMap.put(beanDefinition.getId(), beanDefinition);
		}
	}

	protected boolean addBeanNameMapping(Collection<String> names, String id,
			boolean throwExistError) {
		if (CollectionUtils.isEmpty(names)) {
			return true;
		}

		for (String name : names) {
			BeanDefinition definition = getDefinitionByCache(name);
			if (definition != null) {
				logger.warn("Already exist name:{}, definition:{}", name,
						JSONUtils.toJSONString(definition));
				if (throwExistError) {
					throw new AlreadyExistsException("存在相同名称的映射:"
							+ JSONUtils.toJSONString(definition));
				}
				return false;
			}
		}

		for (String name : names) {
			nameMappingMap.put(name, id);
		}
		return true;
	}

	protected void addBeanFactoryLifeCycle(
			BeanFactoryLifeCycle beanFactoryLifeCycle) throws Exception {
		beanFactoryLifeCycles.add(beanFactoryLifeCycle);
		beanFactoryLifeCycle.init(this, propertyFactory);
	}

	protected void beanFactoryLifeCycleDestroy(
			BeanFactoryLifeCycle beanFactoryLifeCycle) throws Exception {
		beanFactoryLifeCycle.destroy(this, propertyFactory);
	}

	protected void addBeanConfiguration(BeanConfiguration beanConfiguration)
			throws Exception {
		beanConfiguration.init(this, propertyFactory);
		Collection<BeanDefinition> beanDefinitions = beanConfiguration
				.getBeans();
		if (!CollectionUtils.isEmpty(beanDefinitions)) {
			for (BeanDefinition beanDefinition : beanDefinitions) {
				synchronized (beanMap) {
					synchronized (nameMappingMap) {
						addBeanDefinition(beanDefinition, false);
					}
				}
			}
		}
	}

	public void init() throws Exception {
		for (Class<? extends Filter> clazz : InstanceUtils
				.getConfigurationClassList(Filter.class, propertyFactory)) {
			if (!isInstance(clazz)) {
				continue;
			}

			filterNames.add(clazz.getName());
		}

		addBeanConfiguration(new MethodBeanConfiguration());
		addBeanConfiguration(new ServiceBeanConfiguration());
		for (BeanConfiguration configuration : InstanceUtils
				.getConfigurationList(BeanConfiguration.class, this,
						getPropertyFactory())) {
			addBeanConfiguration(configuration);
		}

		propertyFactory.addAll(InstanceUtils.getConfigurationList(
				PropertyFactory.class, this, getPropertyFactory()), true);

		for (BeanFactoryLifeCycle beanFactoryLifeCycle : InstanceUtils
				.getConfigurationList(BeanFactoryLifeCycle.class, this,
						getPropertyFactory())) {
			addBeanFactoryLifeCycle(beanFactoryLifeCycle);
		}
	}

	public void destroy() throws Exception {
		ListIterator<BeanFactoryLifeCycle> iterator = beanFactoryLifeCycles
				.listIterator(beanFactoryLifeCycles.size());
		while (iterator.hasPrevious()) {
			beanFactoryLifeCycleDestroy(iterator.previous());
		}

		synchronized (singletonMap) {
			List<String> beanKeyList = new ArrayList<String>();
			for (Entry<String, Object> entry : singletonMap.entrySet()) {
				beanKeyList.add(entry.getKey());
			}

			ListIterator<String> keyIterator = beanKeyList
					.listIterator(beanKeyList.size());
			while (keyIterator.hasPrevious()) {
				BeanDefinition beanDefinition = getDefinitionByCache(keyIterator
						.previous());
				if (beanDefinition == null) {
					continue;
				}

				Object obj = singletonMap.get(beanDefinition.getId());
				try {
					beanDefinition.destroy(obj);
				} catch (Throwable e) {
					logger.error(e, "destroy error: {}", beanDefinition.getId());
				}
			}
		}
	}

	protected static final class InteranlBeanDefinition implements
			BeanDefinition {
		private final Object instance;
		private final Class<?> targetClass;
		private final Collection<String> names;

		public InteranlBeanDefinition(Object instance, Class<?> targetClass,
				Collection<String> names) {
			this.instance = instance;
			this.targetClass = targetClass;
			this.names = names;
		}

		public String getId() {
			return getTargetClass().getName();
		}

		public Collection<String> getNames() {
			return names;
		}

		public Class<?> getTargetClass() {
			return targetClass;
		}

		public boolean isSingleton() {
			return true;
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws Exception {
			return instance;
		}

		public Object create(Object... params) throws Exception {
			throw new UnsupportedException(getId());
		}

		public Object create(Class<?>[] parameterTypes, Object... params)
				throws Exception {
			throw new UnsupportedException(getId());
		}

		public boolean isProxy() {
			return false;
		}

		public void init(Object bean) throws Exception {
		}

		public void destroy(Object bean) throws Exception {
		}

		public AnnotatedElement getAnnotatedElement() {
			return getTargetClass();
		}
	}
}
