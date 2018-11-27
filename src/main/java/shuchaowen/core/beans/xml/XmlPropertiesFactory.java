package shuchaowen.core.beans.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.BeanParameter;
import shuchaowen.core.beans.EParameterType;
import shuchaowen.core.beans.PropertiesFactory;
import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.exception.BeansException;
import shuchaowen.core.exception.NotFoundException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public class XmlPropertiesFactory implements PropertiesFactory {
	private static final String PROPERTIES_TAG_NAME = "properties";
	private static final String BEANS_TAG_NAME = "beans";

	private final Map<String, XmlProperties> propertiesMap = new HashMap<String, XmlProperties>();
	private final Map<String, Object> propertiesValueMap = new HashMap<String, Object>();
	private final Map<String, BeanParameter> xmlPropertiesMap = new HashMap<String, BeanParameter>();
	private final BeanFactory beanFactory;

	public XmlPropertiesFactory(BeanFactory beanFactory, String beanXml)
			throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
		this.beanFactory = beanFactory;
		if (!StringUtils.isNull(beanXml)) {
			File xml = ConfigUtils.getFile(beanXml);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document document = builder.parse(xml);
			Element root = document.getDocumentElement();
			if (!BEANS_TAG_NAME.equals(root.getTagName())) {
				throw new BeansException("root tag name error [" + root.getTagName() + "]");
			}

			NodeList nhosts = root.getChildNodes();
			List<XmlProperties> xmlPropertiesList = new ArrayList<XmlProperties>();
			for (int i = 0; i < nhosts.getLength(); i++) {
				Node nRoot = nhosts.item(i);
				if (PROPERTIES_TAG_NAME.equalsIgnoreCase(nRoot.getNodeName())) {
					XmlProperties xmlProperties = new XmlProperties(nRoot);
					xmlPropertiesList.add(xmlProperties);
				}
			}

			init(xmlPropertiesList);
		}
	}

	private void init(List<XmlProperties> properties) {
		if (properties != null) {
			for (XmlProperties p : properties) {
				if (!StringUtils.isNull(p.getId())) {
					propertiesMap.put(p.getId(), p);
				}

				String prefix = (p.getPrefix() == null ? "" : p.getPrefix());
				if (p.getProperties() != null) {
					for (Entry<Object, Object> entry : p.getProperties().entrySet()) {
						String key = prefix + entry.getKey();
						if (propertiesValueMap.containsKey(key)) {
							throw new AlreadyExistsException(key);
						}

						propertiesValueMap.put(key, entry.getValue());
					}
				}

				for (BeanParameter beanProperties : p.getOtherPropertiesMap().values()) {
					String key = prefix + beanProperties.getName();
					if (xmlPropertiesMap.containsKey(key)) {
						throw new AlreadyExistsException(key);
					}

					xmlPropertiesMap.put(key, beanProperties);
				}
			}
		}
	}

	public XmlPropertiesFactory(BeanFactory beanFactory, List<XmlProperties> properties) {
		this.beanFactory = beanFactory;
		init(properties);
	}

	public List<BeanParameter> getBeanParameterList(String name) {
		XmlProperties xmlProperties = propertiesMap.get(name);
		if (xmlProperties == null) {
			return null;
		}

		List<BeanParameter> list = new ArrayList<BeanParameter>();
		if (xmlProperties.getProperties() != null) {
			for (Entry<Object, Object> entry : xmlProperties.getProperties().entrySet()) {
				list.add(new BeanParameter(EParameterType.value, null, entry.getKey().toString(),
						entry.getValue().toString(), xmlProperties.getAttrMap()));
			}
		}

		list.addAll(xmlProperties.getOtherPropertiesMap().values());
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperties(String name, Class<T> type) throws Exception {
		if (propertiesMap.containsKey(name)) {
			XmlProperties xmlProperties = propertiesMap.get(name);
			ClassInfo fieldClassInfo = ClassUtils.getClassInfo(type);
			Object obj = beanFactory.get(type);
			for (FieldInfo fieldInfo : fieldClassInfo.getFieldMap().values()) {
				Object v = xmlProperties.getValue(beanFactory, this, fieldInfo.getName(), fieldInfo.getType());
				if (v != null) {
					fieldInfo.set(obj, v);
				}
			}
			return (T) obj;
		} else if (propertiesValueMap.containsKey(name)) {
			Object v = propertiesValueMap.get(name);
			if (v == null) {
				throw new NotFoundException("property[" + name + "] type[" + type.getName() + "]");
			}
			return StringUtils.conversion(v.toString(), type);
		} else if (xmlPropertiesMap.containsKey(name)) {
			BeanParameter beanProperties = xmlPropertiesMap.get(name);
			return (T) beanProperties.parseValue(beanFactory, this, type);
		}

		String v = ConfigUtils.getSystemProperty(name);
		if (v != null) {
			return StringUtils.conversion(v, type);
		}
		throw new NotFoundException("property[" + name + "] type[" + type.getName() + "]");
	}
}
