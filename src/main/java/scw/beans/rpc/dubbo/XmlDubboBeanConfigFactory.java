package scw.beans.rpc.dubbo;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ReferenceConfig;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public class XmlDubboBeanConfigFactory extends AbstractBeanConfigFactory {

	public XmlDubboBeanConfigFactory(BeanFactory beanFactory, PropertyFactory propertyFactory, NodeList nodeList, String[] filterNames)
			throws Exception {
		if (nodeList != null) {
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
					XmlDubboBean xmlDubboBean = new XmlDubboBean(beanFactory, referenceConfig.getInterfaceClass(),
							referenceConfig, filterNames);
					addBean(xmlDubboBean);
				}
			}
		}
	}
}
