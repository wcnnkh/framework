package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.execution.param.ExtractParameterException;
import io.basc.framework.execution.param.ParameterExtractor;
import io.basc.framework.mapper.ParameterDescriptor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AutowireParameterExtractor implements ParameterExtractor {
	private final BeanFactory beanFactory;

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException {
		// TODO Auto-generated method stub
		return null;
	}

}
