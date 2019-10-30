package scw.beans.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.RootFilter;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.aop.ProxyUtils;

public final class XmlDubboBean extends AbstractInterfaceBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;

	public XmlDubboBean(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, ReferenceConfig<?> referenceConfig) {
		super(valueWiredManager, beanFactory, propertyFactory, type);
		this.referenceConfig = referenceConfig;
		init();
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		return (T) ProxyUtils.proxyInstance(referenceConfig.get(), getType(),
				new RootFilter(beanFactory, getType(), null, null));
	}
}
