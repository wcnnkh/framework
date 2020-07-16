package scw.beans.property;

import java.util.Collections;
import java.util.Enumeration;

import org.w3c.dom.NodeList;

import scw.beans.Destroy;
import scw.beans.xml.XmlBeanUtils;
import scw.core.GlobalPropertyFactory;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.EmptyEventRegistration;
import scw.io.ResourceUtils;
import scw.value.Value;
import scw.value.property.BasePropertyFactory;
import scw.value.property.PropertyEvent;

public class XmlPropertyFactory implements BasePropertyFactory, Destroy {
	private AutoRefreshPropertyFactory autoRefreshPropertyFactory;

	public XmlPropertyFactory(String beanXml) {
		this(ResourceUtils.getResourceOperations().isExist(beanXml) ? XmlBeanUtils.getRootNodeList(beanXml) : null);
	}

	public XmlPropertyFactory(NodeList nodeList) {
		if (nodeList == null) {
			return;
		}
		autoRefreshPropertyFactory = new AutoRefreshPropertyFactory(nodeList);
	}

	public Value get(String key) {
		if (autoRefreshPropertyFactory == null) {
			return GlobalPropertyFactory.getInstance().get(key);
		}

		return autoRefreshPropertyFactory.get(key);
	}

	public void destroy() {
		autoRefreshPropertyFactory.destroy();
	}

	public Enumeration<String> enumerationKeys() {
		if (autoRefreshPropertyFactory == null) {
			return Collections.emptyEnumeration();
		}
		return autoRefreshPropertyFactory.enumerationKeys();
	}

	public void unregister(String name) {
		return ;
	}

	public EventRegistration registerListener(String name,
			EventListener<PropertyEvent> eventListener) {
		return new EmptyEventRegistration();
	}

	public void publishEvent(String name, PropertyEvent event) {
		return ;
	}

	public boolean isSupportListener(String key) {
		return false;
	}
}
