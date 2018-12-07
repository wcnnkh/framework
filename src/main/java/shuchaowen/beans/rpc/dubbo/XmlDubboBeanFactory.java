package shuchaowen.beans.rpc.dubbo;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import shuchaowen.beans.AbstractBeanFactory;
import shuchaowen.beans.Bean;
import shuchaowen.beans.property.PropertiesFactory;
import shuchaowen.beans.xml.XmlBeanUtils;
import shuchaowen.common.utils.ClassUtils;
import shuchaowen.core.util.StringUtils;

public class XmlDubboBeanFactory extends AbstractBeanFactory{
	private static final String TAG_NAME = "dubbo:reference";
	
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
				String address = XmlBeanUtils.getAddress(propertiesFactory, node);
				String version = XmlBeanUtils.getVersion(propertiesFactory, node);
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
				
				String packageName = XmlBeanUtils.getPackageName(propertiesFactory, node);
				if(!StringUtils.isNull(packageName)){
					for(Class<?> clz : ClassUtils.getClasses(packageName)){
						if(!clz.isInterface()){
							continue;
						}
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
