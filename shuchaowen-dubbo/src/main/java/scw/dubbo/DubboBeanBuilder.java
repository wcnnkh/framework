package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.BeanFactory;
import scw.beans.builder.ConstructorBeanBuilder;
import scw.core.instance.ConstructorBuilder;
import scw.util.value.property.PropertyFactory;

public class DubboBeanBuilder extends ConstructorBeanBuilder {
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

	@Override
	protected ConstructorBuilder getConstructorBuilder() {
		return null;
	}
}
