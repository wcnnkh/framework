package scw.dubbo;

import org.w3c.dom.Node;

import scw.beans.BeanFactory;
import scw.dom.DomAttributeMapping;
import scw.instance.InstanceUtils;
import scw.mapper.Field;

public class XmlDubboMapping extends DomAttributeMapping {
	private BeanFactory beanFactory;

	public XmlDubboMapping(BeanFactory beanFactory, Node node) {
		super(beanFactory.getEnvironment(), node);
		this.beanFactory = beanFactory;
	}
	
	@Override
	public <T> T newInstance(Class<? extends T> type) {
		return InstanceUtils.NO_ARGS_INSTANCE_FACTORY.getInstance(type);
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
