package scw.beans.property;

import java.util.Timer;

import org.w3c.dom.NodeList;

import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertyFactory;
import scw.core.utils.ResourceUtils;
import scw.core.utils.SystemPropertyUtils;

public class XmlPropertyFactory implements PropertyFactory {
	private AutoRefreshPropertyFactory autoRefreshPropertyFactory;

	public XmlPropertyFactory(String beanXml, Timer timer, long defaultRefreshPeriod) {
		if (!ResourceUtils.isExist(beanXml)) {
			return;
		}

		NodeList nhosts = XmlBeanUtils.getRootNodeList(beanXml);
		autoRefreshPropertyFactory = new AutoRefreshPropertyFactory(nhosts, timer, defaultRefreshPeriod);
	}

	public String getProperty(String key) {
		return autoRefreshPropertyFactory == null ? SystemPropertyUtils.getProperty(key)
				: autoRefreshPropertyFactory.getProperty(key);
	}
}
