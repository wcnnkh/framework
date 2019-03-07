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
		initParameter(xmlPath);
		register();
	}

	public XmlBeanFactory(PropertiesFactory propertiesFactory, String xmlPath, boolean initStatic) throws Exception {
		this.xmlPath = xmlPath;
		this.initStatic = initStatic;
		this.propertiesFactory = propertiesFactory;
		initParameter(xmlPath);
		register();
	}

	private void register() {
		addSingleton(XmlBeanFactory.class.getName(), this);
		registerNameMapping(BeanFactory.class.getName(), XmlBeanFactory.class.getName());
	}

	private void initParameter(String xmlPath) {
		if (!StringUtils.isNull(xmlPath)) {
			Node root = XmlBeanUtils.getRootNode(xmlPath);
			this.packages = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, root, "packages");
			this.filterNames = StringUtils
					.commonSplit(XmlBeanUtils.getNodeAttributeValue(propertiesFactory, root, "filters"));
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

	public void addFirstFilters(String filterName) {
		if (filterName == null || filterName.length() == 0) {
			return;
		}

		if (filterNames == null || filterNames.length == 0) {
			this.filterNames = new String[] { filterName };
		} else {
			String[] arr = new String[filterNames.length + 1];
			arr[0] = filterName;
			System.arraycopy(filterNames, 0, arr, 1, filterNames.length);
			this.filterNames = arr;
		}
	}

	public void addFilters(String filterName) {
		if (filterName == null || filterName.length() == 0) {
			return;
		}

		if (filterNames == null || filterNames.length == 0) {
			this.filterNames = new String[] { filterName };
		} else {
			String[] arr = new String[filterNames.length + 1];
			System.arraycopy(filterNames, 0, arr, 0, filterNames.length);
			arr[filterNames.length] = filterName;
			this.filterNames = arr;
		}
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public boolean isInitStatic() {
		return initStatic;
	}

	public void init() {
		try {
			if (!StringUtils.isNull(xmlPath)) {
				addBeanConfigFactory(new XmlDubboBeanConfigFactory(this, propertiesFactory, xmlPath));
				addBeanConfigFactory(new HttpRPCBeanConfigFactory(this, propertiesFactory, xmlPath));
				addBeanConfigFactory(new XmlBeanConfigFactory(this, propertiesFactory, xmlPath, filterNames));
				addBeanConfigFactory(new ServiceBeanConfigFactory(this, propertiesFactory, packages, filterNames));
			}

			super.init();
			initMethod();
		} catch (Exception e) {
			throw new BeansException(e);
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
		try {
			destroyMethod();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.destroy();
	}
}
