package scw.dubbo;

import org.w3c.dom.Node;

import scw.beans.BeanFactory;
import scw.core.instance.InstanceUtils;
import scw.mapper.FieldContext;
import scw.util.value.property.PropertyFactory;
import scw.xml.XmlAttributeMapper;

public class XmlDubboMapper extends XmlAttributeMapper {
	private BeanFactory beanFactory;

	public XmlDubboMapper(BeanFactory beanFactory, PropertyFactory propertyFactory, Node node) {
		super(propertyFactory, node);
		this.beanFactory = beanFactory;
	}
	
	@Override
	protected <T> T newInstance(Class<? extends T> type) {
		return InstanceUtils.NO_ARGS_INSTANCE_FACTORY.getInstance(type);
	}

	@Override
	protected Object getNodeValue(String name, String value, Class<?> type, FieldContext fieldContext, Node node) {
		if (type.toString().startsWith("org.apache.dubbo.config.") || "registry".equalsIgnoreCase(name)
				|| "registries".equalsIgnoreCase(name) || "ref".equalsIgnoreCase(name)) {
			return beanFactory.getInstance(value);
		}
		return super.getNodeValue(name, value, type, fieldContext, node);
	}
}
