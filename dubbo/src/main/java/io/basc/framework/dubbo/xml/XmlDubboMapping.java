package io.basc.framework.dubbo.xml;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.dom.DomAttributeMapping;
import io.basc.framework.env.Sys;
import io.basc.framework.mapper.Field;

import org.w3c.dom.Node;

public class XmlDubboMapping extends DomAttributeMapping {
	private BeanFactory beanFactory;

	public XmlDubboMapping(BeanFactory beanFactory, Node node) {
		super(beanFactory.getEnvironment(), node);
		this.beanFactory = beanFactory;
	}
	
	@Override
	public <T> T newInstance(Class<? extends T> type) {
		return Sys.getUnsafeInstanceFactory().getInstance(type);
	}
	
	@Override
	protected Object getNodeValue(String name, String value, Class<?> type, Field field, Node node) {
		if (type.toString().startsWith("org.apache.dubbo.config.") || "registry".equalsIgnoreCase(name)
				|| "registries".equalsIgnoreCase(name) || "ref".equalsIgnoreCase(name)) {
			return beanFactory.getInstance(value);
		}
		return super.getNodeValue(name, value, type, field, node);
	}
}