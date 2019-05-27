package scw.beans.rpc.dubbo;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ReferenceConfig;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.xml.XmlBeanUtils;

public final class XmlDubboBeanConfigFactory extends AbstractBeanConfigFactory {
	private static final String TAG_NAME = "dubbo:reference";

	public XmlDubboBeanConfigFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String config)
			throws Exception {
		NodeList rootNodeList = XmlBeanUtils.getRootNode(config).getChildNodes();
		if (rootNodeList != null) {
			for (int x = 0; x < rootNodeList.getLength(); x++) {
				Node node = rootNodeList.item(x);
				if (node == null) {
					continue;
				}

				if (!TAG_NAME.equals(node.getNodeName())) {
					continue;
				}

				List<ReferenceConfig<?>> referenceConfigs = XmlDubboUtils.getReferenceConfigList(propertiesFactory,
						beanFactory, node);
				for (ReferenceConfig<?> referenceConfig : referenceConfigs) {
					XmlDubboBean xmlDubboBean = new XmlDubboBean(beanFactory, referenceConfig.getInterfaceClass(),
							referenceConfig);
					addBean(xmlDubboBean);
				}
			}
		}
	}
}
