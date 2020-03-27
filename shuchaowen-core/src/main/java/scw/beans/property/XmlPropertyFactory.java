package scw.beans.property;

import java.util.Enumeration;

import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.Destroy;
import scw.core.GlobalPropertyFactory;
import scw.io.resource.ResourceUtils;
import scw.util.MultiEnumeration;
import scw.util.value.Value;
import scw.util.value.property.AbstractPropertyFactory;

public class XmlPropertyFactory extends AbstractPropertyFactory implements Destroy {
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
	
	public Value get(String key){
		return autoRefreshPropertyFactory == null? GlobalPropertyFactory.getInstance().get(key):autoRefreshPropertyFactory.get(key);
	}

	public void destroy() {
		autoRefreshPropertyFactory.destroy();
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> enumerationKeys() {
		if(autoRefreshPropertyFactory == null){
			return GlobalPropertyFactory.getInstance().enumerationKeys();
		}
		
		return new MultiEnumeration<String>(autoRefreshPropertyFactory.enumerationKeys(), GlobalPropertyFactory.getInstance().enumerationKeys());
	}
}
