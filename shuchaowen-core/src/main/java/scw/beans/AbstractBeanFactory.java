package scw.beans;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import scw.beans.method.MethodBeanConfiguration;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.instance.AbstractInstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
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

public abstract class AbstractBeanFactory extends
		AbstractInstanceFactory<BeanDefinition> implements BeanFactory, Init,
		Destroy {
	protected static Logger logger = LoggerUtils.getLogger(BeanFactory.class);
	private volatile Map<String, BeanDefinition> beanMap = new HashMap<String, BeanDefinition>();
	private volatile Map<String, String> nameMappingMap = new HashMap<String, String>();
	private volatile HashSet<String> notFoundSet = new HashSet<String>();
	protected final MultiPropertyFactory propertyFactory = new MultiPropertyFactory();
	private final LinkedHashSet<String> filterNames = new LinkedHashSet<String>();
	protected final LinkedList<BeanFactoryLifeCycle> beanFactoryLifeCycles = new LinkedList<BeanFactoryLifeCycle>();

	public AbstractBeanFactory() {
		super();
		addInternalSingletion(this, BeanFactory.class);
		addInternalSingletion(propertyFactory, PropertyFactory.class);
	}

	public final Collection<String> getFilterNames() {
		return Collections.unmodifiableCollection(filterNames);
	}

	public final PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	protected void addBeanConfiguration(BeanConfiguration beanConfiguration)
			throws Exception {
		Collection<BeanDefinition> beanDefinitions = beanConfiguration
				.getBeans(this, getPropertyFactory());
		if (CollectionUtils.isEmpty(beanDefinitions)) {
			return;
		}

		for (BeanDefinition beanDefinition : beanDefinitions) {
			synchronized (beanMap) {
				synchronized (nameMappingMap) {
					addBeanDefinition(beanDefinition, false);
				}
			}
		}
	}

	protected void addBeanDefinition(BeanDefinition beanDefinition,
			boolean throwExistError) {
		BeanDefinition definition = getDefinitionByCache(beanDefinition.getId());
		if(definition != null){
			logger.warn("Already exist id:{}, definition:{}",
					beanDefinition.getId(),
					JSONUtils.toJSONString(beanDefinition));
			if (throwExistError) {
				throw new AlreadyExistsException("存在相同ID的映射:"
						+ JSONUtils.toJSONString(beanDefinition));
			}
			
			return ;
		}

		String[] names = beanDefinition.getNames();
		if (!ArrayUtils.isEmpty(names)) {
			for (String name : names) {
				definition = getDefinitionByCache(name);
				if(definition != null){
					logger.warn("Already exist name:{}, definition:{}", name,
							JSONUtils.toJSONString(beanDefinition));
					if (throwExistError) {
						throw new AlreadyExistsException("存在相同名称的映射:"
								+ JSONUtils.toJSONString(beanDefinition));
					}
					return ;
				}
			}
		
			for(String name : names){
				nameMappingMap.put(name, beanDefinition.getId());
			}
		}
		beanMap.put(beanDefinition.getId(), beanDefinition);
	}

	@Override
	protected void init(long createTime, BeanDefinition definition,
			Object object) throws Exception {
		super.init(createTime, definition, object);
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
		BeanDefinition beanDefinition = getDefinitionByCache(name);
		if (beanDefinition == null) {
			if (notFoundSet.contains(name)) {
				return null;
			}

			synchronized (beanMap) {
				synchronized (nameMappingMap) {
					beanDefinition = getDefinitionByCache(name);
					if (beanDefinition == null) {
						long t = System.currentTimeMillis();
						beanDefinition = autoCreateBeanDefinition(name);
						if (beanDefinition != null) {
							addBeanDefinition(beanDefinition, true);
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

	protected void initBeanFactoryLifeCycle(
			BeanFactoryLifeCycle beanFactoryLifeCycle) throws Exception {
		beanFactoryLifeCycle.init(this, propertyFactory);
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

		long t = System.currentTimeMillis();
		AutoImpl autoImpl = clz.getAnnotation(AutoImpl.class);
		if (autoImpl == null) {
			if (clz.getAnnotation(Ignore.class) != null) {
				return null;
			}
		}

		AutoBean autoBean = AutoBeanUtils.autoBeanService(clz, autoImpl, this,
				getPropertyFactory());
		if (autoBean != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("find [{}] use time {}ms", clz,
						System.currentTimeMillis() - t);
			}
			try {
				return new AutoBeanDefinition(this, getPropertyFactory(), clz,
						autoBean);
			} catch (Exception e) {
				throw new BeansException(clz.getName(), e);
			}
		}
		return null;
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
		for (BeanConfiguration beanConfiguration : InstanceUtils
				.getConfigurationList(BeanConfiguration.class, this,
						getPropertyFactory())) {
			addBeanConfiguration(beanConfiguration);
		}

		propertyFactory.addAll(InstanceUtils.getConfigurationList(
				PropertyFactory.class, this, getPropertyFactory()), true);

		beanFactoryLifeCycles.addAll(InstanceUtils.getConfigurationList(
				BeanFactoryLifeCycle.class, this, getPropertyFactory()));
		Iterator<BeanFactoryLifeCycle> iterator = beanFactoryLifeCycles
				.iterator();
		while (iterator.hasNext()) {
			initBeanFactoryLifeCycle(iterator.next());
		}
	}

	public void destroy() throws Exception {
		ListIterator<BeanFactoryLifeCycle> iterator = beanFactoryLifeCycles
				.listIterator(beanFactoryLifeCycles.size());
		beanFactoryLifeCycles.clear();

		while (iterator.hasPrevious()) {
			iterator.previous().destroy(this, propertyFactory);
		}

		synchronized (singletonMap) {
			List<String> beanKeyList = new ArrayList<String>();
			for (Entry<String, Object> entry : singletonMap.entrySet()) {
				beanKeyList.add(entry.getKey());
			}

			for (String id : beanKeyList) {
				BeanDefinition beanDefinition = getDefinitionByCache(id);
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
	}

	@Override
	public <T> T getInstance(String name) {
		T bean = super.getInstance(name);
		if (bean == null) {
			throw new UnsupportedException(name);
		}
		return bean;
	}

	protected final void addInternalSingletion(Object instance, Class<?> targetClass,
			String... names) {
		singletonMap.put(targetClass.getName(), instance);
		addBeanDefinition(new InteranlBeanDefinition(instance, targetClass,
				names), true);
	}

	private static final class InteranlBeanDefinition implements BeanDefinition {
		private final Object instance;
		private final Class<?> targetClass;
		private final String[] names;

		public InteranlBeanDefinition(Object instance, Class<?> targetClass,
				String... names) {
			this.instance = instance;
			this.targetClass = targetClass;
			this.names = names;
		}

		public String getId() {
			return getTargetClass().getName();
		}

		public String[] getNames() {
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

		@SuppressWarnings("unchecked")
		public <T> T create() throws Exception {
			return (T) instance;
		}

		public <T> T create(Object... params) throws Exception {
			throw new UnsupportedException(getId());
		}

		public <T> T create(Class<?>[] parameterTypes, Object... params)
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
