package io.basc.framework.context.ioc.annotation;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.execution.parameter.ParameterException;
import io.basc.framework.execution.parameter.ParameterExtractor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
public class AutowriedParameterExtractor implements ParameterExtractor {
	private BeanFactory beanFactory;

	@Override
	public boolean canExtractParameter(ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.getTypeDescriptor().isAnnotationPresent(Autowired.class);
	}

	@Override
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ParameterException {
		Autowired autowired = parameterDescriptor.getTypeDescriptor().getAnnotation(Autowired.class);
		String name = autowired.value();
		if (StringUtils.isNotEmpty(name)) {
			return beanFactory.getBean(name, parameterDescriptor.getTypeDescriptor().getType());
		}

		if (beanFactory.isUnique(parameterDescriptor.getTypeDescriptor().getResolvableType())) {
			return beanFactory.getBean(parameterDescriptor.getTypeDescriptor().getResolvableType());
		}

		return beanFactory.getBean(parameterDescriptor.getName(), parameterDescriptor.getTypeDescriptor().getType());
	}

}
