package scw.beans.property;

import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.Destroy;
import scw.core.PropertyFactory;
import scw.core.utils.SystemPropertyUtils;
import scw.resource.ResourceUtils;

public class XmlPropertyFactory implements PropertyFactory, Destroy {
	private AutoRefreshPropertyFactory autoRefreshPropertyFactory;

	public XmlPropertyFactory(String beanXml) {
		this(ResourceUtils.getResourceOperations().isExist(beanXml) ? XmlBeanUtils.getRootNodeList(beanXml) : null);
	}

	public XmlPropertyFactory(NodeList nodeList) {
		if (nodeList == null) {
			return;
		}
		autoRefreshPropertyFactory = new AutoRefreshPropertyFactory(nodeList);
	}

	public String getProperty(String key) {
		return autoRefreshPropertyFactory == null ? SystemPropertyUtils.getProperty(key)
				: autoRefreshPropertyFactory.getProperty(key);
	}

	public void destroy() {
		autoRefreshPropertyFactory.destroy();
	}
}
