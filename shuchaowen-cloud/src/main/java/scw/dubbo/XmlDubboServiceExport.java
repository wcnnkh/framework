package scw.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanFactoryLifeCycle;
import scw.core.instance.annotation.Configuration;
import scw.logger.SplitLineAppend;
import scw.value.property.PropertyFactory;

/**
 * 在{@see XmlDubboBeanConfiguration}中已经处理过Application,
 * <br/>并且XmlBeanConfiguration的执行顺序优先于BeanFactoryLifeCycle
 * 
 * @author shuchaowen
 *
 */
@Configuration(order = Integer.MIN_VALUE)
public class XmlDubboServiceExport extends XmlBeanFactoryLifeCycle {

	@SuppressWarnings("rawtypes")
	@Override
	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		NodeList nodeList = getNodeList();
		if (nodeList == null) {
			return;
		}

		List<ProtocolConfig> protocolConfigs = XmlDubboUtils.parseProtocolConfigList(propertyFactory, nodeList, null);
		if(!protocolConfigs.isEmpty()){
			startLog();
		}
		
		List<ServiceConfig> serviceConfigs = XmlDubboUtils.parseServiceConfigList(propertyFactory, nodeList, null,
				beanFactory);
		if (!serviceConfigs.isEmpty()) {
			if(protocolConfigs.isEmpty()){
				startLog();
			}
			
			for (ServiceConfig<?> config : serviceConfigs) {
				List<ProtocolConfig> protocolConfigsToUse = new ArrayList<ProtocolConfig>(protocolConfigs);
				if (config.getProtocols() != null) {
					protocolConfigsToUse.addAll(config.getProtocols());
				}
				config.setProtocols(protocolConfigsToUse);
				config.export();
			}
		}
		
		if(!protocolConfigs.isEmpty() || !protocolConfigs.isEmpty()){
			endLog();
		}
	}
	
	private void startLog(){
		logger.info(new SplitLineAppend("Start to register Dubbo service"));
	}
	
	private void endLog(){
		logger.info(new SplitLineAppend("Dubbo service registration completed"));
	}

	@Override
	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
	}
}
