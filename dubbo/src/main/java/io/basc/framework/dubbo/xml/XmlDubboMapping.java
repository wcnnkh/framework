package io.basc.framework.dubbo.xml;

import org.w3c.dom.Node;

import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.dom.DomAttributeMapping;
import io.basc.framework.env.Environment;
import io.basc.framework.mapper.Field;

public class XmlDubboMapping extends DomAttributeMapping {
	private Environment environment;

	public XmlDubboMapping(Environment environment, Node node) {
		super(environment, node);
		this.environment = environment;
	}

	@Override
	public <T> T newInstance(Class<? extends T> type) {
		return ReflectionApi.newInstance(type);
	}

	@Override
	protected Object getNodeValue(String name, String value, Class<?> type, Field field, Node node) {
		if (type.toString().startsWith("org.apache.dubbo.config.") || "registry".equalsIgnoreCase(name)
				|| "registries".equalsIgnoreCase(name) || "ref".equalsIgnoreCase(name)) {
			return environment.getInstance(value);
		}
		return super.getNodeValue(name, value, type, field, node);
	}
}
