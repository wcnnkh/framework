package scw.beans.property;

import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.Destroy;
import scw.core.PropertyFactory;
import scw.core.utils.ResourceUtils;
import scw.core.utils.SystemPropertyUtils;

public class XmlPropertyFactory implements PropertyFactory, Destroy {
	private AutoRefreshPropertyFactory autoRefreshPropertyFactory;

	public XmlPropertyFactory(String beanXml, long defaultRefreshPeriod) {
		if (!ResourceUtils.isExist(beanXml)) {
			return;
		}

		NodeList nhosts = XmlBeanUtils.getRootNodeList(beanXml);
		autoRefreshPropertyFactory = new AutoRefreshPropertyFactory(nhosts, defaultRefreshPeriod);
	}

	public String getProperty(String key) {
		return autoRefreshPropertyFactory == null ? SystemPropertyUtils.getProperty(key)
				: autoRefreshPropertyFactory.getProperty(key);
	}

	public void destroy() {
		autoRefreshPropertyFactory.destroy();
	}
}
