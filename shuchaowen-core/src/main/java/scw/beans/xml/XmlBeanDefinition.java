package scw.beans.xml;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.beans.BeanUtils;
import scw.beans.annotation.Proxy;
import scw.beans.auto.AutoBeanUtils;
import scw.core.instance.AutoInstanceBuilder;
import scw.core.instance.InstanceBuilder;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.FieldDefinition;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.lang.NotFoundException;
import scw.lang.UnsupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public final class XmlBeanDefinition implements BeanDefinition {
	private Logger logger = LoggerUtils.getLogger(XmlBeanDefinition.class);

	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;
	private String[] names;
	private final String id;
	private final boolean singleton;
	private final Collection<String> filterNames;
	private final XmlBeanParameter[] properties;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private final boolean proxy;

	private final FieldDefinition[] autowriteFields;
	private final Class<?> type;
	private volatile InstanceBuilder instanceBuilder;

	public XmlBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Node beanNode) throws Exception {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.type = XmlBeanUtils.getClass(beanNode);
		this.names = XmlBeanUtils.getNames(beanNode);
		this.id = XmlBeanUtils.getId(beanNode);
		this.singleton = XmlBeanUtils.isSingleton(beanNode);
		this.filterNames = new LinkedList<String>(
				XmlBeanUtils.getFilters(beanNode));
		Proxy proxy = type.getAnnotation(Proxy.class);
		if (proxy != null) {
			filterNames.addAll(AutoBeanUtils.getProxyNames(proxy));
		}

		this.proxy = CollectionUtils.isEmpty(filterNames) ? BeanUtils
				.checkProxy(type) : true;

		NodeList nodeList = beanNode.getChildNodes();
		this.initMethods = XmlBeanUtils.getInitMethodList(type, nodeList);
		this.destroyMethods = XmlBeanUtils.getDestroyMethodList(type, nodeList);
		this.properties = XmlBeanUtils.getBeanProperties(nodeList);
		this.autowriteFields = BeanUtils.getAutowriteFieldDefinitionList(type)
				.toArray(new FieldDefinition[0]);

		if (!type.isInterface()) {// 可能只是映射
			XmlBeanParameter[] constructorParameters = XmlBeanUtils
					.getConstructorParameters(nodeList);
			if (ArrayUtils.isEmpty(constructorParameters)) {
				this.instanceBuilder = new AutoInstanceBuilder(beanFactory,
						propertyFactory, type,
						ParameterUtils.getParameterDescriptorFactory());
			} else {
				this.instanceBuilder = new XmlInstanceBuilder(beanFactory,
						propertyFactory, type, constructorParameters);
			}
		}
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getTargetClass() {
		return this.type;
	}

	public boolean isSingleton() {
		return this.singleton;
	}

	public boolean isProxy() {
		return this.proxy;
	}

	private scw.aop.Proxy getProxy() {
		return BeanUtils.createProxy(beanFactory, type, filterNames, null);
	}

	private void setProperties(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(properties)) {
			return;
		}

		for (XmlBeanParameter beanProperties : properties) {
			Field field = ReflectionUtils.getField(type,
					beanProperties.getName(), true);
			if (field == null) {
				continue;
			}

			ReflectionUtils.setFieldValue(type, field, bean, beanProperties
					.parseValue(beanFactory, propertyFactory,
							field.getGenericType()));
		}
	}

	public void init(Object bean) throws Exception {
		BeanUtils.autowired(beanFactory, propertyFactory, type, bean,
				Arrays.asList(autowriteFields));
		setProperties(bean);

		if (!ArrayUtils.isEmpty(initMethods)) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		BeanUtils.init(bean);
	}

	public void destroy(Object bean) throws Exception {
		if (!ArrayUtils.isEmpty(destroyMethods)) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		BeanUtils.destroy(bean);
	}

	protected Object createInternal(Constructor<?> constructor, Object[] args)
			throws Exception {
		Object bean;
		if (isProxy()) {
			scw.aop.Proxy proxy = getProxy();
			bean = proxy.create(constructor.getParameterTypes(), args);
		} else {
			bean = constructor.newInstance(args);
		}
		return bean;
	}

	@SuppressWarnings("unchecked")
	public final <T> T create() throws Exception {
		if (!isInstance()) {
			throw new UnsupportedException(id);
		}

		if (isProxy() && getTargetClass().isInterface()) {
			if (CollectionUtils.isEmpty(filterNames)) {
				logger.warn("{} is an interface, but there is no proxy.", type);
			}
			return (T) getProxy().create();
		}

		return (T) createInternal(instanceBuilder.getConstructor(),
				instanceBuilder.getArgs());
	}

	public String[] getNames() {
		return names;
	}

	@SuppressWarnings("unchecked")
	public final <T> T create(Object... params) throws Exception {
		Constructor<T> constructor = (Constructor<T>) ReflectionUtils
				.findConstructorByParameters(getTargetClass(), false, params);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		return (T) createInternal(constructor, params);
	}

	@SuppressWarnings("unchecked")
	public final <T> T create(Class<?>[] parameterTypes, Object... params)
			throws Exception {
		Constructor<?> constructor = ReflectionUtils.findConstructor(
				getTargetClass(), false, parameterTypes);
		if (constructor == null) {
			throw new NotFoundException(getId() + "找不到指定的构造方法");
		}

		return (T) createInternal(constructor, params);
	}

	public boolean isInstance() {
		return instanceBuilder.getConstructor() != null;
	}

	public AnnotatedElement getAnnotatedElement() {
		return getTargetClass();
	}
}
