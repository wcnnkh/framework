package scw.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetricsConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.SslConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.w3c.dom.NodeList;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.boot.ApplicationPostProcessor;
import scw.boot.ConfigurableApplication;
import scw.context.annotation.Provider;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.logger.SplitLineAppend;

/**
 * 暴露dubbo服务
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Integer.MIN_VALUE)
public class XmlDubboInitializer implements ApplicationPostProcessor {
	private static Logger logger = LoggerFactory
			.getLogger(XmlDubboInitializer.class);
	
	@Override
	public void postProcessApplication(ConfigurableApplication application)
			throws Throwable {
		ConfigurableBeanFactory beanFactory = application.getBeanFactory();
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
				beanFactory.registerDefinition(
						xmlDubboBean.getId(), xmlDubboBean);
			}

			//export service
			List<ProtocolConfig> protocolConfigs = XmlDubboUtils
					.parseProtocolConfigList(application.getEnvironment(),
							nodeList, null);
			if (!protocolConfigs.isEmpty()) {
				startLog();
			}

			@SuppressWarnings("rawtypes")
			List<ServiceConfig> serviceConfigs = XmlDubboUtils
					.parseServiceConfigList(application.getEnvironment(),
							nodeList, null, beanFactory, beanFactory);
			if (!serviceConfigs.isEmpty()) {
				if (protocolConfigs.isEmpty()) {
					startLog();
				}

				for (ServiceConfig<?> config : serviceConfigs) {
					List<ProtocolConfig> protocolConfigsToUse = new ArrayList<ProtocolConfig>(
							protocolConfigs);
					if (config.getProtocols() != null) {
						protocolConfigsToUse.addAll(config.getProtocols());
					}
					config.setProtocols(protocolConfigsToUse);
					config.export();
				}
			}

			if (!protocolConfigs.isEmpty() || !protocolConfigs.isEmpty()) {
				endLog();
			}
		}
	}
	
	private void startLog() {
		logger.info(new SplitLineAppend("Start to register Dubbo service"));
	}

	private void endLog() {
		logger.info(new SplitLineAppend("Dubbo service registration completed"));
	}
}
