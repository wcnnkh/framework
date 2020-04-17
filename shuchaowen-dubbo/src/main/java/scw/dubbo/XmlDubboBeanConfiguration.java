package scw.dubbo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanConfiguration;
import scw.core.instance.annotation.Configuration;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.logger.SplitLineAppend;
import scw.util.value.property.PropertyFactory;

@Configuration
public final class XmlDubboBeanConfiguration extends XmlBeanConfiguration {
	private static Logger logger = LoggerUtils
			.getLogger(XmlDubboBeanConfiguration.class);

	public Collection<BeanDefinition> getBeans(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		NodeList nodeList = getNodeList();
		if (nodeList == null) {
			return null;
		}
		XmlDubboUtils.initConfig(propertyFactory, beanFactory, nodeList);

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

		DubboUtils.registerDubboShutdownHook();

		List<BeanDefinition> definitions = new LinkedList<BeanDefinition>();
		for (int x = 0; x < getNodeList().getLength(); x++) {
			Node node = getNodeList().item(x);
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
				definitions.add(xmlDubboBean);
			}
		}
		return definitions;
	}
}
