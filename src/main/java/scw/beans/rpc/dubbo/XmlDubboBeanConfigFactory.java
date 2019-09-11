package scw.beans.rpc.dubbo;

import java.util.List;

import org.apache.dubbo.config.ReferenceConfig;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.Destroy;
import scw.core.PropertyFactory;

public class XmlDubboBeanConfigFactory extends AbstractBeanConfigFactory {

	public XmlDubboBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, NodeList nodeList, String[] filterNames) throws Exception {
		if (nodeList != null) {
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
							referenceConfig.getInterfaceClass(), filterNames, referenceConfig);
					addBean(xmlDubboBean);
					addDestroy(new ReferenceConfigDestory(referenceConfig));
				}
			}
		}
	}

	private static final class ReferenceConfigDestory implements Destroy {
		private ReferenceConfig<?> referenceConfig;

		public ReferenceConfigDestory(ReferenceConfig<?> referenceConfig) {
			this.referenceConfig = referenceConfig;
		}

		public void destroy() {
			referenceConfig.destroy();
		}

	}
}
