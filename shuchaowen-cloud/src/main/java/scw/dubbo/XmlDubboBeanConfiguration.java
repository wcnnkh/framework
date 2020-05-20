package scw.dubbo;

import java.util.List;

import org.apache.dubbo.config.ReferenceConfig;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanConfiguration;
import scw.core.instance.annotation.Configuration;
import scw.value.property.PropertyFactory;

@Configuration(order=Integer.MIN_VALUE)
public final class XmlDubboBeanConfiguration extends XmlBeanConfiguration {
	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		NodeList nodeList = getNodeList();
		if (nodeList == null) {
			return;
		}
		
		XmlDubboUtils.initConfig(propertyFactory, beanFactory, nodeList);
		for (int x = 0; x < nodeList.getLength(); x++) {
			Node node = nodeList.item(x);
			if (node == null) {
				continue;
			}

			if (!DubboUtils.isReferenceNode(node)) {
				continue;
			}

			List<ReferenceConfig<?>> referenceConfigs = XmlDubboUtils
					.getReferenceConfigList(propertyFactory, beanFactory, node);
			for (ReferenceConfig<?> referenceConfig : referenceConfigs) {
				XmlDubboBean xmlDubboBean = new XmlDubboBean(beanFactory,
						propertyFactory, referenceConfig.getInterfaceClass(),
						referenceConfig);
				beanDefinitions.add(xmlDubboBean);
			}
		}
	}
}
