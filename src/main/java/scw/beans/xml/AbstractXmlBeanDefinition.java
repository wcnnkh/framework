package scw.beans.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanMethod;
import scw.core.Destroy;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.utils.ArrayUtils;

public abstract class AbstractXmlBeanDefinition implements BeanDefinition {
	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;
	private String[] names;
	private final String id;
	private final boolean singleton;
	private final String[] filterNames;
	private final BeanMethod[] initMethods;
	private final BeanMethod[] destroyMethods;
	private final Class<?> type;

	public AbstractXmlBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Node beanNode, String[] filterNames)
			throws Exception {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.type = XmlBeanUtils.getClass(beanNode);
		this.names = XmlBeanUtils.getNames(beanNode);
		this.id = XmlBeanUtils.getId(beanNode);
		this.singleton = XmlBeanUtils.isSingleton(beanNode);
		this.filterNames = XmlBeanUtils.getFilters(beanNode, filterNames);
		NodeList nodeList = beanNode.getChildNodes();
		this.initMethods = XmlBeanUtils.getInitMethodList(type, nodeList);
		this.destroyMethods = XmlBeanUtils.getDestroyMethodList(type, nodeList);
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getType() {
		return this.type;
	}

	public boolean isSingleton() {
		return this.singleton;
	}

	public void init(Object bean) throws Exception {
		if (!ArrayUtils.isEmpty(initMethods)) {
			for (BeanMethod method : initMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		if (bean instanceof Init) {
			((Init) bean).init();
		}
	}

	public String[] getFilterNames() {
		return filterNames;
	}

	public void destroy(Object bean) throws Exception {
		if (!ArrayUtils.isEmpty(destroyMethods)) {
			for (BeanMethod method : destroyMethods) {
				method.invoke(bean, beanFactory, propertyFactory);
			}
		}

		if (bean instanceof Destroy) {
			((Destroy) bean).destroy();
		}
	}

	public String[] getNames() {
		return names;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}
}
