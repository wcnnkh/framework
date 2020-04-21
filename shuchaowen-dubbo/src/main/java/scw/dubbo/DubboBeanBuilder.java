package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.AbstractBeanBuilder;
import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public class DubboBeanBuilder extends AbstractBeanBuilder {
	private final ReferenceConfig<?> referenceConfig;

	public DubboBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass,
			ReferenceConfig<?> referenceConfig) {
		super(beanFactory, propertyFactory, targetClass);
		this.referenceConfig = referenceConfig;
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() throws Exception {
		return createInstanceProxy(referenceConfig.get(), getTargetClass(),
				new Class<?>[] { referenceConfig.getInterfaceClass() })
				.create();
	}
}
