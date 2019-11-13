package scw.beans;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.dubbo.DubboUtils;
import scw.beans.property.XmlPropertyFactory;
import scw.beans.rpc.HttpRpcBeanConfigFactory;
import scw.beans.xml.XmlBeanConfigFactory;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanUtils;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public class XmlBeanFactory extends AbstractBeanFactory {
	private NodeList nodeList;

	public XmlBeanFactory(String xmlConfigPath) {
		if (ResourceUtils.isExist(xmlConfigPath)) {
			Node root = XmlBeanUtils.getRootNode(xmlConfigPath);
			addFilterName(Arrays
					.asList(StringUtils.commonSplit(XMLUtils.getNodeAttributeValue(propertyFactory, root, "filters"))));
			this.nodeList = XMLUtils.getChildNodes(root, true);
		}
		propertyFactory.add(new XmlPropertyFactory(nodeList));
	}

	public final NodeList getNodeList() {
		return nodeList;
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		Object bean = super.getInstance(name);
		if (bean == null) {
			throw new BeansException("not found [" + name + "]");
		}
		return (T) bean;
	}

	protected String getServicePackage() {
		String p = BeanUtils.getServiceAnnotationPackage(propertyFactory);
		return p == null ? BeanUtils.getAnnotationPackage(propertyFactory) : p;
	}

	public void init() {
		try {
			if (nodeList != null) {
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

				DubboUtils.exportService(this, propertyFactory, nodeList);
				DubboUtils.registerDubboShutdownHook();
			} else {
				super.init();
			}
		} catch (Exception e) {
			logger.error(e, "初始化异常");
			throw new BeansException();
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
		if (nodeList == null) {
			return;
		}

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

	private void destroyMethod(NodeList nodeList) throws Exception {
		if (nodeList == null) {
			return;
		}

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
			destroyMethod(nodeList);
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
