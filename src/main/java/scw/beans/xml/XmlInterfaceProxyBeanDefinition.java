package scw.beans.xml;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.w3c.dom.Node;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.utils.XMLUtils;

public class XmlInterfaceProxyBeanDefinition extends AbstractXmlBeanDefinition {
	private String proxy;

	public XmlInterfaceProxyBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Node beanNode, String[] filterNames)
			throws Exception {
		super(beanFactory, propertyFactory, beanNode, filterNames);
		this.proxy = XMLUtils.getRequireNodeAttributeValue(beanNode, "proxy");
	}

	public boolean isProxy() {
		return true;
	}

	public void autowrite(Object bean) throws Exception {
		// ignore
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		InvocationHandler invocationHandler = getBeanFactory().getInstance(
				proxy);
		Object newProxyInstance = Proxy
				.newProxyInstance(getType().getClassLoader(),
						new Class[] { getType() }, invocationHandler);
		return (T) BeanUtils.proxyInterface(getBeanFactory(), getType(),
				newProxyInstance, getFilterNames());
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Object... params) {
		InvocationHandler invocationHandler = getBeanFactory().getInstance(
				proxy, params);
		Object newProxyInstance = Proxy
				.newProxyInstance(getType().getClassLoader(),
						new Class[] { getType() }, invocationHandler);
		return (T) BeanUtils.proxyInterface(getBeanFactory(), getType(),
				newProxyInstance, getFilterNames());
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<?>[] parameterTyeps, Object... params) {
		InvocationHandler invocationHandler = getBeanFactory().getInstance(
				proxy, parameterTyeps, params);
		Object newProxyInstance = Proxy
				.newProxyInstance(getType().getClassLoader(),
						new Class[] { getType() }, invocationHandler);
		return (T) BeanUtils.proxyInterface(getBeanFactory(), getType(),
				newProxyInstance, getFilterNames());
	}

	public boolean isInstance() {
		return true;
	}

}