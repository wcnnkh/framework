package scw.dubbo;

import java.util.List;

import org.apache.dubbo.config.ReferenceConfig;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.beans.xml.XmlBeanConfiguration;
import scw.core.instance.annotation.Configuration;
import scw.util.value.property.PropertyFactory;

@Configuration
public class XmlDubboBeanConfiguration extends AbstractBeanConfiguration implements XmlBeanConfiguration {

	public void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			NodeList nodeList) throws Exception {
		XmlDubboUtils.initConfig(propertyFactory, beanFactory, nodeList);
		for (int x = 0; x < nodeList.getLength(); x++) {
			Node node = nodeList.item(x);
			if (node == null) {
				continue;
			}

			if (!DubboUtils.isReferenceNode(node)) {
				continue;
			}

			List<ReferenceConfig<?>> referenceConfigs = XmlDubboUtils.getReferenceConfigList(propertyFactory,
					beanFactory, node);
			for (ReferenceConfig<?> referenceConfig : referenceConfigs) {
				XmlDubboBean xmlDubboBean = new XmlDubboBean(valueWiredManager, beanFactory, propertyFactory,
						referenceConfig.getInterfaceClass(), referenceConfig);
				addBean(xmlDubboBean);
				// addDestroy(new ReferenceConfigDestory(referenceConfig));
			}
		}
		addInit(new XmlDubboServiceExort(propertyFactory, beanFactory, nodeList));
		DubboUtils.registerDubboShutdownHook();
	}

	/*
	 * private static final class ReferenceConfigDestory implements Destroy {
	 * private ReferenceConfig<?> referenceConfig;
	 * 
	 * public ReferenceConfigDestory(ReferenceConfig<?> referenceConfig) {
	 * this.referenceConfig = referenceConfig; }
	 * 
	 * public void destroy() { referenceConfig.destroy(); } }
	 */
}
