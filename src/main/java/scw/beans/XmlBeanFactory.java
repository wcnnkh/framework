package scw.beans;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.dubbo.DubboUtils;
import scw.beans.rpc.HttpRpcBeanConfigFactory;
import scw.beans.xml.XmlBeanConfigFactory;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertyFactory;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public class XmlBeanFactory extends AbstractBeanFactory {
	private final String xmlPath;

	public XmlBeanFactory(PropertyFactory propertyFactory, String xmlPath, int defaultValueRefreshPeriod)
			throws Exception {
		super(propertyFactory, defaultValueRefreshPeriod);
		this.xmlPath = xmlPath;
		if (ResourceUtils.isExist(xmlPath)) {
			Node root = XmlBeanUtils.getRootNode(xmlPath);
			addFilterName(Arrays
					.asList(StringUtils.commonSplit(XMLUtils.getNodeAttributeValue(propertyFactory, root, "filters"))));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		Object bean = super.getInstance(name);
		if (bean == null) {
			throw new BeansException("not found [" + name + "]");
		}
		return (T) bean;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	protected String getServicePackage() {
		String p = BeanUtils.getServiceAnnotationPackage(propertyFactory);
		return p == null ? BeanUtils.getAnnotationPackage(propertyFactory) : p;
	}

	public void init() {
		try {
			if (ResourceUtils.isExist(xmlPath)) {
				NodeList nodeList = XmlBeanUtils.getRootNodeList(xmlPath);
				addBeanConfigFactory(
						new XmlBeanConfigFactory(getValueWiredManager(), this, propertyFactory, nodeList, "bean"));
				addBeanConfigFactory(new ServiceBeanConfigFactory(getValueWiredManager(), this, propertyFactory,
						getServicePackage()));
				addBeanConfigFactory(
						new HttpRpcBeanConfigFactory(getValueWiredManager(), this, propertyFactory, nodeList));
				BeanConfigFactory dubboBeanConfigFactory = DubboUtils
						.getReferenceBeanConfigFactory(getValueWiredManager(), this, propertyFactory, nodeList);
				if (dubboBeanConfigFactory != null) {
					addBeanConfigFactory(dubboBeanConfigFactory);
				}
				super.init();
				initMethod(nodeList);
			} else {
				super.init();
			}
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	private void doInit(BeanDefinition beanDefinition, Node node, String className) throws Exception {
		XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(beanDefinition.getType(), node);
		if (Modifier.isStatic(xmlBeanMethodInfo.getMethod().getModifiers())) {
			// 静态方法
			xmlBeanMethodInfo.invoke(null, this, propertyFactory);
		} else {
			xmlBeanMethodInfo.invoke(getInstance(className), this, propertyFactory);
		}
	}

	private void initMethod(NodeList nodeList) throws Exception {
		for (int a = 0; a < nodeList.getLength(); a++) {
			final Node n = nodeList.item(a);
			if ("init".equalsIgnoreCase(n.getNodeName())) {
				final String className = XMLUtils.getRequireNodeAttributeValue(propertyFactory, n, "class");
				final BeanDefinition beanDefinition = getBeanDefinition(className);
				if (beanDefinition == null) {
					throw new NotFoundException(className);
				}

				Thread thread = new Thread() {
					public void run() {
						try {
							doInit(beanDefinition, n, className);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
				};

				if (XMLUtils.getBooleanValue(n, "async", false)) {
					thread.start();
				} else {
					thread.run();
				}
			}
		}
	}

	private void destroyMethod() throws Exception {
		if (!ResourceUtils.isExist(xmlPath)) {
			return;
		}

		NodeList nodeList = XmlBeanUtils.getRootNodeList(xmlPath);
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if ("destroy".equalsIgnoreCase(n.getNodeName())) {
				String className = XMLUtils.getRequireNodeAttributeValue(propertyFactory, n, "class");
				BeanDefinition beanDefinition = getBeanDefinition(className);
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(beanDefinition.getType(), n);
				if (Modifier.isStatic(xmlBeanMethodInfo.getMethod().getModifiers())) {
					// 静态方法
					xmlBeanMethodInfo.invoke(null, this, propertyFactory);
				} else {
					xmlBeanMethodInfo.invoke(getInstance(className), this, propertyFactory);
				}
			}
		}
	}

	public void destroy() {
		try {
			destroyMethod();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.destroy();
	}

	@Override
	public <T> T getInstance(String name, Class<?>[] parameterTypes, Object... params) {
		T bean = super.getInstance(name, parameterTypes, params);
		if (bean == null) {
			throw new BeansException("not found [" + name + "]");
		}
		return bean;
	}

	@Override
	public <T> T getInstance(String name, Object... params) {
		T bean = super.getInstance(name, params);
		if (bean == null) {
			throw new BeansException("not found [" + name + "]");
		}
		return bean;
	}

	@Override
	protected String getInitStaticPackage() {
		String init = BeanUtils.getInitStaticPackage(propertyFactory);
		return init == null ? BeanUtils.getAnnotationPackage(propertyFactory) : init;
	}
}
