package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.util.value.property.PropertyFactory;

public final class XmlDubboBean extends AbstractInterfaceBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;

	public XmlDubboBean(BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, ReferenceConfig<?> referenceConfig) {
		super(beanFactory, propertyFactory, type);
		this.referenceConfig = referenceConfig;
		init();
	}

	public Object create() {
		return BeanUtils
				.createProxy(beanFactory, referenceConfig.getInterfaceClass(), referenceConfig.get(), null, null)
				.create();
	}
}
