package scw.beans;

import java.lang.reflect.Modifier;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.XmlDubboBeanConfigFactory;
import scw.beans.rpc.http.HttpRpcBeanConfigFactory;
import scw.beans.xml.XmlBeanConfigFactory;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertiesFactory;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public final class XmlBeanFactory extends AbstractBeanFactory {
	private final PropertiesFactory propertiesFactory;
	private String[] filterNames;
	private String packages;
	private final boolean initStatic;// 是否初始化静态方法
	private final String xmlPath;

	public XmlBeanFactory(String xmlPath, boolean initStatic) throws Exception {
		this(new XmlPropertiesFactory(xmlPath), xmlPath, initStatic);
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
			this.packages = XMLUtils.getNodeAttributeValue(propertiesFactory, root, "packages");
			this.filterNames = StringUtils
					.commonSplit(XMLUtils.getNodeAttributeValue(propertiesFactory, root, "filters"));
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
				addBeanConfigFactory(new HttpRpcBeanConfigFactory(this, propertiesFactory, xmlPath));
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
			if ("init".equalsIgnoreCase(n.getNodeName())) {
				String className = XMLUtils.getRequireNodeAttributeValue(propertiesFactory, n, "class");
				BeanDefinition beanDefinition = getBeanDefinition(className);
				if (beanDefinition == null) {
					throw new NotFoundException(className);
				}

				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(beanDefinition.getType(), n);
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
			if ("destroy".equalsIgnoreCase(n.getNodeName())) {
				String className = XMLUtils.getRequireNodeAttributeValue(propertiesFactory, n, "class");
				BeanDefinition beanDefinition = getBeanDefinition(className);
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(beanDefinition.getType(), n);
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

	@Override
	public <T> T get(String name, Class<?>[] parameterTypes, Object... params) {
		T bean = super.get(name, parameterTypes, params);
		if (bean == null) {
			throw new BeansException("not found [" + name + "]");
		}
		return bean;
	}

	@Override
	public <T> T get(String name, Object... params) {
		T bean = super.get(name, params);
		if (bean == null) {
			throw new BeansException("not found [" + name + "]");
		}
		return bean;
	}

	public boolean isProxy(String name) {
		BeanDefinition beanDefinition = getBeanDefinition(name);
		return beanDefinition.isProxy();
	}
}
