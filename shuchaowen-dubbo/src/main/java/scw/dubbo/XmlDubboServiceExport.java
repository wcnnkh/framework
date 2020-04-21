package scw.dubbo;

import java.util.List;

import org.apache.dubbo.config.ServiceConfig;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanFactoryLifeCycle;
import scw.core.instance.annotation.Configuration;
import scw.logger.SplitLineAppend;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public class XmlDubboServiceExport extends XmlBeanFactoryLifeCycle {

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		NodeList nodeList = getNodeList();

		for (int x = 0; x < nodeList.getLength(); x++) {
			Node node = nodeList.item(x);
			if (node == null) {
				continue;
			}

			if (DubboUtils.isServiceNode(node)) {
				logger.info(new SplitLineAppend("开始注册dubbo服务"));
				List<ServiceConfig<?>> serviceConfigs = XmlDubboUtils
						.getServiceConfigList(propertyFactory, beanFactory,
								node);
				for (ServiceConfig<?> serviceConfig : serviceConfigs) {
					serviceConfig.export();
				}
				logger.info(new SplitLineAppend("dubbo服务注册完成"));
			}
		}

	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		DubboUtils.registerDubboShutdownHook();
	}

}
