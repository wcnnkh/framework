package scw.beans.auto;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.instance.AutoInstanceBuilder;
import scw.core.instance.InstanceBuilder;
import scw.core.parameter.ParameterUtils;
import scw.util.value.property.PropertyFactory;

public class SimpleAutoBean extends AbstractSimpleAutoBean {
	private InstanceBuilder instanceBuilder;

	public SimpleAutoBean(BeanFactory beanFactory, Class<?> type,
			PropertyFactory propertyFactory) {
		super(beanFactory, type);
		this.instanceBuilder = new AutoInstanceBuilder(beanFactory,
				propertyFactory, type,
				ParameterUtils.getParameterDescriptorFactory());
	}

	public boolean isInstance() {
		return instanceBuilder.getConstructor() != null;
	}

	@Override
	protected Class<?>[] getParameterTypes() {
		return instanceBuilder.getConstructor() == null ? null
				: instanceBuilder.getConstructor().getParameterTypes();
	}

	@Override
	protected Object[] getParameters() throws Exception {
		return instanceBuilder.getArgs();
	}

	@Override
	protected Collection<String> getFilterNames() {
		return null;
	}
}
