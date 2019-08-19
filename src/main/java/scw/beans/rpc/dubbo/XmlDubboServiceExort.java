package scw.beans.rpc.dubbo;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ServiceConfig;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.logger.LoggerUtils;

public class XmlDubboServiceExort implements Runnable {
	private final PropertyFactory propertyFactory;
	private final BeanFactory beanFactory;
	private final NodeList nodeList;

	public XmlDubboServiceExort(PropertyFactory propertyFactory, BeanFactory beanFactory, NodeList nodeList) {
		this.propertyFactory = propertyFactory;
		this.beanFactory = beanFactory;
		this.nodeList = nodeList;
	}

	public void run() {
		if (nodeList != null) {
			for (int x = 0; x < nodeList.getLength(); x++) {
				Node node = nodeList.item(x);
				if (node == null) {
					continue;
				}

				if (DubboUtils.isServiceNode(node)) {
					LoggerUtils.info(XmlDubboServiceExort.class, "-------开始注册dubbo服务-------");
					List<ServiceConfig<?>> serviceConfigs = XmlDubboUtils.getServiceConfigList(propertyFactory,
							beanFactory, node);
					for (ServiceConfig<?> serviceConfig : serviceConfigs) {
						serviceConfig.export();
					}
					LoggerUtils.info(XmlDubboServiceExort.class, "-------dubbo服务注册完成-------");
				}
			}
		}
	}
}
