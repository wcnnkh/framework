package scw.beans;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.method.MethodBeanConfiguration;
import scw.beans.property.XmlPropertyFactory;
import scw.beans.xml.DefaultXmlBeanConfiguration;
import scw.beans.xml.XmlBeanConfiguration;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanUtils;
import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.io.resource.ResourceUtils;
import scw.lang.NotFoundException;

public class XmlBeanFactory extends AbstractBeanFactory {
	private NodeList nodeList;
	private XmlPropertyFactory xmlPropertyFactory;

	public XmlBeanFactory(String xmlConfigPath) {
		if (ResourceUtils.getResourceOperations().isExist(xmlConfigPath)) {
			Node root = XmlBeanUtils.getRootNode(xmlConfigPath);
			addFilterName(Arrays.asList(StringUtils.commonSplit(XMLUtils
					.getNodeAttributeValue(propertyFactory, root, "filters"))));
			this.nodeList = XMLUtils.getChildNodes(root, true);
		}
		this.xmlPropertyFactory = new XmlPropertyFactory(nodeList);
		propertyFactory.add(xmlPropertyFactory);
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

	private void appendNameMapping(NodeList nodeList) {
		if (nodeList == null) {
			return;
		}

		for (int i = 0, len = nodeList.getLength(); i < len; i++) {
			Node node = nodeList.item(i);
			if (node == null) {
				continue;
			}

			if ("mapping".equalsIgnoreCase(node.getNodeName())) {
				addBeanNameMapping(StringUtils.commonSplit(XMLUtils
						.getRequireNodeAttributeValue(propertyFactory, node,
								"name")),
						XMLUtils.getRequireNodeAttributeValueOrNodeContent(
								propertyFactory, node, "id"));
			}
		}
	}

	public void init() {
		try {
			appendNameMapping(nodeList);
			if (nodeList != null) {
				addBeanConfiguration(new DefaultXmlBeanConfiguration(
						getValueWiredManager(), this, propertyFactory,
						nodeList, "bean"));
				addBeanConfiguration(new ServiceBeanConfiguration(
						getValueWiredManager(), this, propertyFactory,
						BeanUtils.getScanAnnotationPackageName()));
				addBeanConfiguration(new MethodBeanConfiguration(
						getValueWiredManager(), this, propertyFactory,
						BeanUtils.getScanAnnotationPackageName()));
				for (BeanConfiguration beanConfiguration : InstanceUtils
						.getConfigurationList(BeanConfiguration.class, this, propertyFactory)) {
					if (beanConfiguration instanceof XmlBeanConfiguration) {
						((XmlBeanConfiguration) beanConfiguration).init(
								getValueWiredManager(), this, propertyFactory,
								nodeList);
					} else if (beanConfiguration instanceof SimpleBeanConfiguration) {
						((SimpleBeanConfiguration) beanConfiguration).init(
								getValueWiredManager(), this, propertyFactory);
					}
					addBeanConfiguration(beanConfiguration);
				}
			}

			super.init();
			initMethod(nodeList);
		} catch (Exception e) {
			throw new BeansException("初始化异常", e);
		}
	}

	private void doInit(BeanDefinition beanDefinition, Node node,
			String className) throws Exception {
		XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(
				beanDefinition.getTargetClass(), node);
		if (Modifier.isStatic(xmlBeanMethodInfo.getMethod().getModifiers())) {
			// 静态方法
			xmlBeanMethodInfo.invoke(null, this, propertyFactory);
		} else {
			xmlBeanMethodInfo.invoke(getInstance(className), this,
					propertyFactory);
		}
	}

	private void initMethod(NodeList nodeList) throws Exception {
		if (nodeList == null) {
			return;
		}

		for (int a = 0; a < nodeList.getLength(); a++) {
			final Node n = nodeList.item(a);
			if ("init".equalsIgnoreCase(n.getNodeName())) {
				final String className = XMLUtils.getRequireNodeAttributeValue(
						propertyFactory, n, "class");
				final BeanDefinition beanDefinition = getDefinition(className);
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
				String className = XMLUtils.getRequireNodeAttributeValue(
						propertyFactory, n, "class");
				BeanDefinition beanDefinition = getDefinition(className);
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(
						beanDefinition.getTargetClass(), n);
				if (Modifier.isStatic(xmlBeanMethodInfo.getMethod()
						.getModifiers())) {
					// 静态方法
					xmlBeanMethodInfo.invoke(null, this, propertyFactory);
				} else {
					xmlBeanMethodInfo.invoke(getInstance(className), this,
							propertyFactory);
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
		xmlPropertyFactory.destroy();
		super.destroy();
	}

	@Override
	public <T> T getInstance(String name, Class<?>[] parameterTypes,
			Object... params) {
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
}
