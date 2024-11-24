package io.basc.framework.autoconfigure.beans.factory;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.ioc.AutowireParameterExtractor;
import io.basc.framework.core.execution.param.ParameterDescriptor;

public class ValueAutowireParameterExtractor implements AutowireParameterExtractor {

	@Override
	public boolean canExtractParameter(BeanFactory source, ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.getTypeDescriptor().isAnnotationPresent(Value.class);
	}

	@Override
	public Object extractParameter(BeanFactory source, ParameterDescriptor parameterDescriptor) {

		// TODO Auto-generated method stub
		return null;
	}

}
