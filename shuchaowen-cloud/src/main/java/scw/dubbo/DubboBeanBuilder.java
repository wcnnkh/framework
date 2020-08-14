package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public class DubboBeanBuilder extends AbstractBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;

	public DubboBeanBuilder(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			ReferenceConfig<?> referenceConfig) {
		super(beanFactory, propertyFactory, targetClass);
		this.referenceConfig = referenceConfig;
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() throws Exception {
		return createInstanceProxy(referenceConfig.get(), getTargetClass(),
				new Class<?>[] { referenceConfig.getInterfaceClass() }).create();
	}
}
