package scw.beans.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.instance.AutoConstructorBuilder;
import scw.core.instance.ConstructorBuilder;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.UnsupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public final class XmlBeanDefinition extends AbstractBeanDefinition {
	private Logger logger = LoggerUtils.getLogger(XmlBeanDefinition.class);
	private final String[] names;
	private final XmlBeanParameter[] properties;
	private volatile ConstructorBuilder instanceBuilder;

	public XmlBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Node beanNode) throws Exception {
		super(beanFactory, propertyFactory, XmlBeanUtils.getClass(beanNode));
		init();
		
		String id = XmlBeanUtils.getId(beanNode);
		if (!StringUtils.isEmpty(id)) {
			this.id = id;
		}

		this.names = XmlBeanUtils.getNames(beanNode);
		this.singleton = XmlBeanUtils.isSingleton(beanNode) ? true
				: this.singleton;
		filterNames.addAll(XmlBeanUtils.getFilters(beanNode));
		NodeList nodeList = beanNode.getChildNodes();
		this.initMethods.addAll(XmlBeanUtils.getInitMethodList(
				getTargetClass(), nodeList));
		this.destroyMethods.addAll(XmlBeanUtils
				.getDestroyMethodList(getTargetClass(), nodeList));
		this.properties = XmlBeanUtils.getBeanProperties(nodeList);

		if (!getTargetClass().isInterface()) {// 可能只是映射
			XmlBeanParameter[] constructorParameters = XmlBeanUtils
					.getConstructorParameters(nodeList);
			if (ArrayUtils.isEmpty(constructorParameters)) {
				this.instanceBuilder = new AutoConstructorBuilder(beanFactory,
						propertyFactory, getTargetClass(),
						ParameterUtils.getParameterDescriptorFactory());
			} else {
				this.instanceBuilder = new XmlConstructorBuilder(beanFactory,
						propertyFactory, getTargetClass(),
						constructorParameters);
			}
		}
	}

	private scw.aop.Proxy getProxy() {
		if (filterNames.isEmpty()) {
			logger.warn("{} is an interface, but there is no proxy.",
					getTargetClass());
		}
		return BeanUtils.createProxy(beanFactory, getTargetClass(),
				filterNames, null);
	}

	private void setProperties(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(properties)) {
			return;
		}

		for (XmlBeanParameter beanProperties : properties) {
			Field field = ReflectionUtils.getField(getTargetClass(),
					beanProperties.getName(), true);
			if (field == null) {
				continue;
			}

			ReflectionUtils.setFieldValue(getTargetClass(), field, bean,
					beanProperties.parseValue(beanFactory, propertyFactory,
							field.getGenericType()));
		}
	}

	public void init(Object bean) throws Exception {
		setProperties(bean);
		super.init(bean);
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

	public Object create() throws Exception {
		if (!isInstance()) {
			throw new UnsupportedException(getId());
		}

		if (isProxy() && getTargetClass().isInterface()) {
			if (CollectionUtils.isEmpty(filterNames)) {
				logger.warn("{} is an interface, but there is no proxy.",
						getTargetClass());
			}
			return getProxy().create();
		}

		return createInternal(instanceBuilder.getConstructor(),
				instanceBuilder.getArgs());
	}

	public String[] getNames() {
		return names;
	}

	public boolean isInstance() {
		return instanceBuilder.getConstructor() != null;
	}
}
