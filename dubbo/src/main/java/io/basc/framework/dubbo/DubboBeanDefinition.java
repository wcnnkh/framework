package io.basc.framework.dubbo;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;

import org.apache.dubbo.config.ReferenceConfig;

public class DubboBeanDefinition extends DefaultBeanDefinition {
	private final ReferenceConfig<?> referenceConfig;

	public DubboBeanDefinition(ConfigurableBeanFactory beanFactory, Class<?> targetClass,
			ReferenceConfig<?> referenceConfig) {
		super(beanFactory, targetClass);
		this.referenceConfig = referenceConfig;
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() {
		return createInstanceProxy(referenceConfig.get(), getTargetClass(),
				new Class<?>[] { referenceConfig.getInterfaceClass() }).create();
	}
}
