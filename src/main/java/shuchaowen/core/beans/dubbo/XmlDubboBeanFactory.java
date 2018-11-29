package shuchaowen.core.beans.dubbo;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.AbstractBeanFactory;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.beans.xml.XmlBeanUtils;

public class XmlDubboBeanFactory extends AbstractBeanFactory{
	public XmlDubboBeanFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String config){
		NodeList rootNodeList = XmlBeanUtils.getRootNode(config);
		if (rootNodeList != null) {
			for (int x = 0; x < rootNodeList.getLength(); x++) {
				Node node = rootNodeList.item(x);
				if (node == null) {
					continue;
				}
				
				if(!"dubbo:reference".equals(node.getNodeName())){
					continue;
				}
				
			}
		}
	}
	
	@Override
	protected Bean newBean(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
