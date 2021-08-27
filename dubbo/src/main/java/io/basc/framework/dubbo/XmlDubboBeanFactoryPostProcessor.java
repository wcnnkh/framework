package io.basc.framework.dubbo;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.xml.XmlBeanFactory;
import io.basc.framework.context.annotation.Provider;

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

@Provider
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
							nodeList, null, beanFactory.getClassesLoaderFactory())) {
				DubboBeanDefinition xmlDubboBean = new DubboBeanDefinition(
						beanFactory, config.getInterfaceClass(), config);
				beanFactory.registerDefinition(xmlDubboBean);
			}
		}
	}

}
