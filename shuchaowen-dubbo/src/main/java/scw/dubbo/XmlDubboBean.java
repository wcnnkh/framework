package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.property.ValueWiredManager;
import scw.util.value.property.PropertyFactory;

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
		return (T) BeanUtils
				.createProxy(beanFactory, referenceConfig.getInterfaceClass(), referenceConfig.get(), null, null)
				.create();
	}
}
