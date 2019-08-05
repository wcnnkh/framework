package scw.beans;

import java.lang.reflect.Modifier;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.property.XmlPropertiesFactory;
import scw.beans.rpc.dubbo.DubboUtils;
import scw.beans.rpc.http.HttpRpcBeanConfigFactory;
import scw.beans.xml.XmlBeanConfigFactory;
import scw.beans.xml.XmlBeanMethodInfo;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertiesFactory;
import scw.core.exception.BeansException;
import scw.core.exception.NotFoundException;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public class XmlBeanFactory extends AbstractBeanFactory {
	private final PropertiesFactory propertiesFactory;
	private String[] filterNames;
	private final String xmlPath;

	public XmlBeanFactory(String xmlPath) throws Exception {
		this(new XmlPropertiesFactory(xmlPath), xmlPath);
	}

	public XmlBeanFactory(PropertiesFactory propertiesFactory, String xmlPath) throws Exception {
		this.xmlPath = xmlPath;
		this.propertiesFactory = propertiesFactory;
		initParameter(xmlPath);
		register();
	}

	private void register() {
		addSingleton(PropertiesFactory.class.getName(), propertiesFactory);
		addSingleton(BeanFactory.class.getName(), this);
	}

	private void initParameter(String xmlPath) {
		if (ResourceUtils.isExist(xmlPath)) {
			Node root = XmlBeanUtils.getRootNode(xmlPath);
			this.filterNames = StringUtils
					.commonSplit(XMLUtils.getNodeAttributeValue(propertiesFactory, root, "filters"));
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

	public final PropertiesFactory getPropertiesFactory() {
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

	public String getXmlPath() {
		return xmlPath;
	}

	protected String getServicePackage() {
		String p = BeanUtils.getServiceAnnotationPackage(propertiesFactory);
		return p == null ? BeanUtils.getAnnotationPackage(propertiesFactory) : p;
	}

	public void init() {
		try {
			if (ResourceUtils.isExist(xmlPath)) {
				NodeList nodeList = XmlBeanUtils.getRootNodeList(xmlPath);
				BeanConfigFactory dubboBeanConfigFactory = DubboUtils.getReferenceBeanConfigFactory(this,
						propertiesFactory, nodeList, filterNames);
				if (dubboBeanConfigFactory != null) {
					addBeanConfigFactory(dubboBeanConfigFactory);
				}

				addBeanConfigFactory(new HttpRpcBeanConfigFactory(this, propertiesFactory, nodeList, filterNames));
				addBeanConfigFactory(new XmlBeanConfigFactory(this, propertiesFactory, nodeList, filterNames));
				addBeanConfigFactory(new ServiceBeanConfigFactory(this, propertiesFactory, getServicePackage(), filterNames));
				super.init();
				initMethod(nodeList);
			} else {
				super.init();
			}
		} catch (Exception e) {
			throw new BeansException(e);
		}
	}

	private void initMethod(NodeList nodeList) throws Exception {
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
					xmlBeanMethodInfo.invoke(getInstance(className), this, propertiesFactory);
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
				String className = XMLUtils.getRequireNodeAttributeValue(propertiesFactory, n, "class");
				BeanDefinition beanDefinition = getBeanDefinition(className);
				XmlBeanMethodInfo xmlBeanMethodInfo = new XmlBeanMethodInfo(beanDefinition.getType(), n);
				if (Modifier.isStatic(xmlBeanMethodInfo.getMethod().getModifiers())) {
					// 静态方法
					xmlBeanMethodInfo.invoke(null, this, propertiesFactory);
				} else {
					xmlBeanMethodInfo.invoke(getInstance(className), this, propertiesFactory);
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

	public boolean isProxy(String name) {
		BeanDefinition beanDefinition = getBeanDefinition(name);
		return beanDefinition.isProxy();
	}

	@Override
	protected String getInitStaticPackage() {
		String init = BeanUtils.getInitStaticPackage(propertiesFactory);
		return init == null ? BeanUtils.getAnnotationPackage(propertiesFactory) : init;
	}
}
