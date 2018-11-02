package shuchaowen.core.beans.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanInfoConfiguration;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.StringUtils;

public class XmlBeansConfiguration implements BeanInfoConfiguration {
	private static final String BEANS_TAG_NAME = "beans";
	private static final String BEANS_ANNOTATION = "packages";
	private static final String BEAN_TAG_NAME = "bean";
	private static final String PROPERTIES_TAG_NAME = "properties";
	private static final String BEAN_FACTORY_TAG_NAME = "factory";
	
	private final Map<String, Bean> beanMap = new HashMap<String, Bean>();
	private final Map<String, String> nameMappingMap = new HashMap<String, String>();
	private String packageNames;
	private PropertiesFactory propertiesFactory;
	private List<String> beanFactoryList;
	//private final BeanFactory beanFactory;
	
	public XmlBeansConfiguration(BeanFactory beanFactory, String beanXml) throws Exception{
		//this.beanFactory = beanFactory;
		
		if(!StringUtils.isNull(beanXml)){
			File xml = ConfigUtils.getFile(beanXml);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document document = builder.parse(xml);
			Element root = document.getDocumentElement();
			if (!BEANS_TAG_NAME.equals(root.getTagName())) {
				throw new BeansException("root tag name error [" + root.getTagName() + "]");
			}
			
			if(root.getAttributes() != null){
				Node annotationNode = root.getAttributes().getNamedItem(BEANS_ANNOTATION);
				if(annotationNode != null){
					this.packageNames = annotationNode.getNodeValue();
				}
			}
			
			NodeList nhosts = root.getChildNodes();
			List<XmlProperties> xmlPropertiesList = new ArrayList<XmlProperties>();
			for(int i=0; i<nhosts.getLength(); i++){
				Node nRoot = nhosts.item(i);
				if(PROPERTIES_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())){
					XmlProperties xmlProperties = new XmlProperties(nRoot);
					xmlPropertiesList.add(xmlProperties);
				}
			}
			this.propertiesFactory = new XmlPropertiesFactory(beanFactory, xmlPropertiesList);

			for (int i = 0; i < nhosts.getLength(); i++) {
				Node nRoot = nhosts.item(i);
				if(BEAN_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())){
					Bean bean = new XmlBean(beanFactory, propertiesFactory, nRoot);
					if(beanMap.containsKey(bean.getId())){
						throw new BeansException(bean.getId() + " Already exist");
					}
					beanMap.put(bean.getId(), bean);
					
					if(bean.getNames() != null){
						for(String n : bean.getNames()){
							if(nameMappingMap.containsKey(n)){
								throw new BeansException(n + " Already exist");
							}
							nameMappingMap.put(n, bean.getId());
						}
					}
				}else if(BEAN_FACTORY_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())){
					Node node = nRoot.getAttributes().getNamedItem("value");
					String name = node == null? null:node.getNodeValue();
					if(name == null){
						name = nRoot.getNodeValue();
					}
					
					if(!StringUtils.isNull(name)){
						if(beanFactoryList == null){
							beanFactoryList = new ArrayList<String>();
						}
						beanFactoryList.add(name);
					}
				}
			}
		}
	}

	public Bean getBean(Class<?> type) {
		return getBean(type.getName());
	}

	public Bean getBean(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		String v = nameMappingMap.get(realName);
		return v == null? beanMap.get(realName):beanMap.get(v);
	}

	public boolean contains(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		return nameMappingMap.containsKey(realName) || beanMap.containsKey(realName);
	}

	public String getPackageNames() {
		return packageNames;
	}
}
