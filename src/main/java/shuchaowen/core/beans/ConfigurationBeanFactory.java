package shuchaowen.core.beans;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import shuchaowen.core.beans.exception.BeansException;
import shuchaowen.core.beans.xml.XmlBeanInfoConfiguration;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.StringUtils;

public class ConfigurationBeanFactory implements BeanFactory {
	private static final String BEANS_TAG_NAME = "beans";
	private static final String BEANS_ANNOTATION = "packages";
	
	private final AnnotationBeanInfoConfiguration annotationBeanInfoConfiguration;
	private XmlBeanInfoConfiguration xmlBeanInfoConfiguration;
	private volatile Map<String, Object> singletonMap = new HashMap<String, Object>();
	private String packageNames;

	public ConfigurationBeanFactory(ConfigFactory configFactory, String config) throws Exception {
		if(!StringUtils.isNull(config)){
			Node root = getRootNode(config);
			if(root.getAttributes() != null){
				Node annotationNode = root.getAttributes().getNamedItem(BEANS_ANNOTATION);
				if(annotationNode != null){
					this.packageNames = annotationNode.getNodeValue();
				}
			}
			
			this.xmlBeanInfoConfiguration = new XmlBeanInfoConfiguration(this, configFactory, root);
		}
		this.annotationBeanInfoConfiguration = new AnnotationBeanInfoConfiguration(this, packageNames);
		registerSingleton(this.getClass(), this);
	}
	
	public String getPackageNames() {
		return packageNames;
	}



	public static Node getRootNode(String beanXml) throws Exception{
		File xml = ConfigUtils.getFile(beanXml);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		Document document = builder.parse(xml);
		Element root = document.getDocumentElement();
		if (!BEANS_TAG_NAME.equals(root.getTagName())) {
			throw new BeansException("root tag name error [" + root.getTagName() + "]");
		}
		return root;
	}
	
	//注册一个单例
	public void registerSingleton(Class<?> type, Object bean){
		registerSingleton(type.getName(), bean);
	}
	
	public void registerSingleton(String name, Object bean){
		String realName = ClassUtils.getCGLIBRealClassName(name);
		if(singletonMap.containsKey(realName)){
			throw new ShuChaoWenRuntimeException("singleton Already exist");//单例已经存在
		}
		
		synchronized (singletonMap) {
			if(singletonMap.containsKey(realName)){
				throw new ShuChaoWenRuntimeException("singleton Already exist");//单例已经存在
			}
			
			singletonMap.put(realName, bean);
			Bean b = getBean(realName);
			try {
				b.wrapper(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Bean getBeanInfoByRealName(String realName){
		Bean beanInfo = xmlBeanInfoConfiguration == null? null:xmlBeanInfoConfiguration.getBean(realName);
		if (beanInfo == null) {
			beanInfo = annotationBeanInfoConfiguration.getBean(realName);
		}
		return beanInfo;
	}

	public Bean getBean(String name) {
		return getBeanInfoByRealName(ClassUtils.getCGLIBRealClassName(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		String realName = ClassUtils.getCGLIBRealClassName(name);
		T t = (T) singletonMap.get(realName);
		if(t != null){
			return t;
		}

		if (!contains(realName)) {
			if (name.equals(BeanFactory.class.getName())) {
				return (T) this;
			}
		}

		Bean beanInfo = getBeanInfoByRealName(realName);
		try {
			return get(beanInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T get(Bean beanInfo) throws Exception {
		Object bean;
		String name = ClassUtils.getCGLIBRealClassName(beanInfo.getType().getName());
		if (beanInfo.isSingleton()) {
			bean = singletonMap.get(name);
			if (bean == null) {
				synchronized (singletonMap) {
					bean = singletonMap.get(name);
					if (bean == null) {
						bean = beanInfo.newInstance();
						singletonMap.put(name, bean);
						beanInfo.wrapper(bean);
					}
				}
			}
		} else {
			bean = beanInfo.newInstance();
			beanInfo.wrapper(bean);
		}
		return (T) bean;
	}

	public <T> T get(Class<T> type) {
		return get(type.getName());
	}

	public boolean contains(String name) {
		return annotationBeanInfoConfiguration.contains(name) || (xmlBeanInfoConfiguration != null && xmlBeanInfoConfiguration.contains(name));
	}

	public void init() {
		try {
			BeanUtils.initStatic(this, ClassUtils.getClasses(packageNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		HashSet<Class<?>> tagSet = new HashSet<Class<?>>();
		for (Entry<String, Object> entry : singletonMap.entrySet()) {
			if(contains(entry.getKey())){
				if(tagSet.contains(entry.getValue().getClass())){
					continue;
				}
				
				tagSet.add(entry.getValue().getClass());
				try {
					Bean beanInfo = getBean(entry.getKey());
					beanInfo.destroy(entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {
			BeanUtils.destroyStaticMethod(ClassUtils.getClasses(packageNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Bean getBean(Class<?> type) {
		return getBean(type.getName());
	}
}
