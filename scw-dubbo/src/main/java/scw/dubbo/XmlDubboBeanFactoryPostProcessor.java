package scw.dubbo;

import java.util.List;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.SslConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.context.annotation.Provider;

@Provider(order=Integer.MIN_VALUE)
public class XmlDubboBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		if (beanFactory instanceof XmlBeanFactory) {
			NodeList nodeList = ((XmlBeanFactory) beanFactory).getNodeList();
			if (nodeList == null) {
				return;
			}

			List<RegistryConfig> registryConfigs = XmlDubboUtils
					.parseRegistryConfigList(beanFactory.getEnvironment(),
							nodeList, null);
			for (ApplicationConfig config : XmlDubboUtils
					.parseApplicationConfigList(beanFactory.getEnvironment(),
							nodeList, null)) {
				config.setRegistries(registryConfigs);
				ApplicationModel.getConfigManager().setApplication(config);
			}

			for (SslConfig config : XmlDubboUtils.parseSslConfigList(
					beanFactory.getEnvironment(), nodeList)) {
				ApplicationModel.getConfigManager().setSsl(config);
			}

			for (MetricsConfig config : XmlDubboUtils.parseMetricsConfigList(
					beanFactory.getEnvironment(), nodeList)) {
				ApplicationModel.getConfigManager().setMetrics(config);
			}

			for (ModuleConfig config : XmlDubboUtils.parseModuleConfigList(
					beanFactory.getEnvironment(), nodeList)) {
				ApplicationModel.getConfigManager().setModule(config);
			}

			for (MonitorConfig config : XmlDubboUtils.parseMonitorConfigList(
					beanFactory.getEnvironment(), nodeList)) {
				ApplicationModel.getConfigManager().setMonitor(config);
			}

			for (ReferenceConfig<?> config : XmlDubboUtils
					.parseReferenceConfigList(beanFactory.getEnvironment(),
							nodeList, null, beanFactory)) {
				DubboBeanDefinition xmlDubboBean = new DubboBeanDefinition(
						beanFactory, config.getInterfaceClass(), config);
				beanFactory.registerDefinition(xmlDubboBean);
			}
		}
	}

}
