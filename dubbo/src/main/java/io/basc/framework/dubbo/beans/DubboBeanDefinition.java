package io.basc.framework.dubbo.beans;

import org.apache.dubbo.config.ReferenceConfig;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;

public class DubboBeanDefinition extends FactoryBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;

	public DubboBeanDefinition(BeanFactory beanFactory, Class<?> targetClass, ReferenceConfig<?> referenceConfig) {
		super(beanFactory, targetClass);
		this.referenceConfig = referenceConfig;
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() {
		return createInstanceProxy(getAop(), referenceConfig.get(), getTypeDescriptor().getType(),
				new Class<?>[] { referenceConfig.getInterfaceClass() }).create();
	}
}
