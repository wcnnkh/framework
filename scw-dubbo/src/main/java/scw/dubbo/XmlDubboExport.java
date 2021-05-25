package scw.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.SplitLine;

public class XmlDubboExport implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(XmlDubboExport.class);
	private final BeanFactory beanFactory;
	private final NodeList nodeList;
	
	public XmlDubboExport(BeanFactory beanFactory, NodeList nodeList){
		this.beanFactory = beanFactory;
		this.nodeList = nodeList;
	}
	
	@Override
	public void run() {
		List<ProtocolConfig> protocolConfigs = XmlDubboUtils
				.parseProtocolConfigList(beanFactory.getEnvironment(),
						nodeList, null);
		if (!protocolConfigs.isEmpty()) {
			startLog();
		}

		@SuppressWarnings("rawtypes")
		List<ServiceConfig> serviceConfigs = XmlDubboUtils
				.parseServiceConfigList(beanFactory.getEnvironment(),
						nodeList, null, beanFactory, beanFactory.getClassesLoaderFactory());
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

	private void startLog() {
		logger.info(new SplitLine("Start to register Dubbo service").toString());
	}

	private void endLog() {
		logger.info(new SplitLine("Dubbo service registration completed").toString());
	}
}
