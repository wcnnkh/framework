package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;

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
