package scw.beans;

import java.lang.reflect.Modifier;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.property.PropertiesFactory;
import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.XmlDubboBeanConfigFactory;
import scw.beans.rpc.http.HttpRPCBeanConfigFactory;
import scw.beans.xml.XmlBeanConfigFactory;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanUtils;
import scw.common.exception.BeansException;
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.StringUtils;

public final class XmlBeanFactory extends AbstractBeanFactory {
	private static final String INIT_METHOD_TAG_NAME = "init";
	private static final String DESTROY_METHOD_TAG_NAME = "destroy";

	private final PropertiesFactory propertiesFactory;
	private String[] filterNames;
	private String packages;
	private final boolean initStatic;// 是否初始化静态方法
	private final String xmlPath;

	public XmlBeanFactory(String xmlPath, boolean initStatic) throws Exception {
		this.xmlPath = xmlPath;
		this.propertiesFactory = new XmlPropertiesFactory(xmlPath);
		this.initStatic = initStatic;
		initXmlDefaultBeanFactory(xmlPath);
	}

	public XmlBeanFactory(PropertiesFactory propertiesFactory, String xmlPath, boolean initStatic) throws Exception {
		this.xmlPath = xmlPath;
		this.initStatic = initStatic;
		this.propertiesFactory = propertiesFactory;
		initXmlDefaultBeanFactory(xmlPath);
	}

	private void initXmlDefaultBeanFactory(String xmlPath) throws Exception {
		if (!StringUtils.isNull(xmlPath)) {
			Node root = XmlBeanUtils.getRootNode(xmlPath);
			this.packages = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, root, "packages");
			this.filterNames = StringUtils
					.commonSplit(XmlBeanUtils.getNodeAttributeValue(propertiesFactory, root, "filters"));
			addBeanConfigFactory(new XmlDubboBeanConfigFactory(propertiesFactory, xmlPath));
			addBeanConfigFactory(new HttpRPCBeanConfigFactory(propertiesFactory, xmlPath));
			addBeanConfigFactory(new XmlBeanConfigFactory(this, propertiesFactory, xmlPath, filterNames));
			addBeanConfigFactory(new ServiceBeanConfigFactory(this, propertiesFactory, packages, filterNames));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		Object bean = super.get(name);
		if (bean == null) {
			throw new BeansException("not found [" + name + "]");
		}
		return (T) bean;
	}

	public PropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
	}

	public String[] getFilterNames() {
		return filterNames;
	}

	public String getPackages() {
		return packages;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public boolean isInitStatic() {
		return initStatic;
	}

	public void init() {
		super.init();
		try {

			initMethod();
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	private void initMethod() throws Exception {
		Node root = XmlBeanUtils.getRootNode(xmlPath);
		NodeList nodeList = root.getChildNodes();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (INIT_METHOD_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {
				String className = XmlBeanUtils.getRequireNodeAttributeValue(propertiesFactory, n, "class");
				Bean bean = getBean(className);
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(bean.getType(), n);
				if (Modifier.isStatic(xmlBeanMethodInfo.getMethod().getModifiers())) {
					// 静态方法
					xmlBeanMethodInfo.invoke(null, this, propertiesFactory);
				} else {
					xmlBeanMethodInfo.invoke(get(className), this, propertiesFactory);
				}
			}
		}
	}

	private void destroyMethod() throws Exception {
		Node root = XmlBeanUtils.getRootNode(xmlPath);
		NodeList nodeList = root.getChildNodes();
		for (int a = 0; a < nodeList.getLength(); a++) {
			Node n = nodeList.item(a);
			if (DESTROY_METHOD_TAG_NAME.equalsIgnoreCase(n.getNodeName())) {
				String className = XmlBeanUtils.getRequireNodeAttributeValue(propertiesFactory, n, "class");
				Bean bean = getBean(className);
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(bean.getType(), n);
				if (Modifier.isStatic(xmlBeanMethodInfo.getMethod().getModifiers())) {
					// 静态方法
					xmlBeanMethodInfo.invoke(null, this, propertiesFactory);
				} else {
					xmlBeanMethodInfo.invoke(get(className), this, propertiesFactory);
				}
			}
		}
	}

	public void destroy() {
		super.destroy();

		try {
			destroyMethod();
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
