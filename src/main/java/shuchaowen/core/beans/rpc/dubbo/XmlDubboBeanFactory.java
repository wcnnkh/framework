package shuchaowen.core.beans.rpc.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import shuchaowen.core.beans.AbstractBeanFactory;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.property.PropertiesFactory;
import shuchaowen.core.beans.xml.XmlBeanUtils;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.StringUtils;

public class XmlDubboBeanFactory extends AbstractBeanFactory{
	private static final String TAG_NAME = "dubbo:reference";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public XmlDubboBeanFactory(PropertiesFactory propertiesFactory, String config) throws ClassNotFoundException{
		NodeList rootNodeList = XmlBeanUtils.getRootNode(config).getChildNodes();
		if (rootNodeList != null) {
			for (int x = 0; x < rootNodeList.getLength(); x++) {
				Node node = rootNodeList.item(x);
				if (node == null) {
					continue;
				}
				
				if(!TAG_NAME.equals(node.getNodeName())){
					continue;
				}
				
				String name = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "name");
				String address = XmlBeanUtils.getRequireNodeAttributeValue(propertiesFactory, node, "address");
				String version = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "version");
				boolean singleton = XmlBeanUtils.isSingleton(node);
				boolean check = XmlBeanUtils.getBooleanValue(propertiesFactory, node, "check", false);
				
				ApplicationConfig application = new ApplicationConfig(name);
				List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();	
				String[] list = StringUtils.commonSplit(address);
				for (String add : list) {
					RegistryConfig registryConfig = new RegistryConfig();
					registryConfig.setAddress(add);
					registryConfigs.add(registryConfig);
				}
				
				String packageName = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "package");
				if(!StringUtils.isNull(packageName)){
					for(Class<?> clz : ClassUtils.getClasses(packageName)){
						if(!clz.isInterface()){
							continue;
						}
						
						ReferenceConfig referenceConfig = new ReferenceConfig();
						referenceConfig.setApplication(application);
						referenceConfig.setRegistries(registryConfigs);
						referenceConfig.setInterface(clz);
						referenceConfig.setVersion(version);
						referenceConfig.setCheck(false);
						XmlDubboBean xmlDubboBean = new XmlDubboBean(application, registryConfigs, version, clz, singleton, check);
						putBean(xmlDubboBean.getId(), xmlDubboBean);
					}
				}
				
				NodeList nodeList = node.getChildNodes();
				for(int i=0; i<nodeList.getLength(); i++){
					Node referenceNode = nodeList.item(i);
					if(referenceNode == null){
						continue;
					}
					
					XmlDubboBean xmlDubboBean = new XmlDubboBean(propertiesFactory, application, registryConfigs, referenceNode);
					putBean(xmlDubboBean.getId(), xmlDubboBean);
				}
			}
		}
	}
	
	@Override
	protected Bean newBean(String name) throws Exception {
		return null;
	}
}
