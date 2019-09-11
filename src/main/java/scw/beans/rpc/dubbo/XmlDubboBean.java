package scw.beans.rpc.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;

public final class XmlDubboBean extends AbstractInterfaceBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;

	public XmlDubboBean(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, String[] filterNames, ReferenceConfig<?> referenceConfig) {
		super(valueWiredManager, beanFactory, propertyFactory, type, filterNames);
		this.referenceConfig = referenceConfig;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		return (T) BeanUtils.proxyInterface(beanFactory, type, referenceConfig.get(), filterNames);
	}
}
