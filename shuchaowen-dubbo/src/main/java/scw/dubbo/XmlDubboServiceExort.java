package scw.dubbo;

import java.util.List;

import org.apache.dubbo.config.ServiceConfig;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.core.Init;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.logger.SplitLineAppend;
import scw.util.value.property.PropertyFactory;

public class XmlDubboServiceExort implements Init {
	private static Logger logger = LoggerUtils.getLogger(XmlDubboServiceExort.class);
	private final PropertyFactory propertyFactory;
	private final BeanFactory beanFactory;
	private final NodeList nodeList;

	public XmlDubboServiceExort(PropertyFactory propertyFactory, BeanFactory beanFactory, NodeList nodeList) {
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
		this.nodeList = nodeList;
	}

	public void init() {
		if (nodeList != null) {
			XmlDubboUtils.initConfig(propertyFactory, beanFactory, nodeList);

			for (int x = 0; x < nodeList.getLength(); x++) {
				Node node = nodeList.item(x);
				if (node == null) {
					continue;
				}

				if (DubboUtils.isServiceNode(node)) {
					logger.info(new SplitLineAppend("开始注册dubbo服务"));
					List<ServiceConfig<?>> serviceConfigs = XmlDubboUtils.getServiceConfigList(propertyFactory,
							beanFactory, node);
					for (ServiceConfig<?> serviceConfig : serviceConfigs) {
						serviceConfig.export();
					}
					logger.info(new SplitLineAppend("dubbo服务注册完成"));
				}
			}
		}
	}
}
