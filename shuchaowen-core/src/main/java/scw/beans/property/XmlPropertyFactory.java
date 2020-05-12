package scw.beans.property;

import java.util.Collections;
import java.util.Enumeration;

import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.Destroy;
import scw.core.GlobalPropertyFactory;
import scw.io.ResourceUtils;
import scw.value.Value;
import scw.value.property.BasePropertyFactory;

public class XmlPropertyFactory implements BasePropertyFactory, Destroy {
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

	public Value get(String key) {
		if (autoRefreshPropertyFactory == null) {
			return GlobalPropertyFactory.getInstance().get(key);
		}

		return autoRefreshPropertyFactory.get(key);
	}

	public void destroy() {
		autoRefreshPropertyFactory.destroy();
	}

	public Enumeration<String> enumerationKeys() {
		if (autoRefreshPropertyFactory == null) {
			return Collections.emptyEnumeration();
		}
		return autoRefreshPropertyFactory.enumerationKeys();
	}
}
