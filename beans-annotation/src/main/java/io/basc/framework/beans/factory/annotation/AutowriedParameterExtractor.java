package io.basc.framework.beans.factory.annotation;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.execution.param.ExtractParameterException;
import io.basc.framework.execution.param.ParameterExtractor;
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
	public Object extractParameter(ParameterDescriptor parameterDescriptor) throws ExtractParameterException {
		Autowired autowired = parameterDescriptor.getTypeDescriptor().getAnnotation(Autowired.class);
		String name = autowired.value();
		if (StringUtils.isNotEmpty(name)) {
			return beanFactory.getBean(name, parameterDescriptor.getTypeDescriptor().getType());
		}

		// 使用类型或名称
		return beanFactory.getBeanProvider(parameterDescriptor.getTypeDescriptor().getResolvableType()).getUnique()
				.orElseGet(() -> {
					return beanFactory.getBean(parameterDescriptor.getName(),
							parameterDescriptor.getTypeDescriptor().getType());
				});
	}
}
