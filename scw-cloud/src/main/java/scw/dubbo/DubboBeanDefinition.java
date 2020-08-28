package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.DefaultBeanDefinition;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public class DubboBeanDefinition extends DefaultBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;

	public DubboBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
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
