package io.basc.framework.execution.aop.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.ArrayUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对调用参数默认值的处理
 * 
 * @author wcnnkh
 *
 */
@Data
@NoArgsConstructor
public abstract class DefaultValueExecutionInterceptor implements ExecutionInterceptor, ConversionServiceAware {
	private ConversionService conversionService;

	@Override
	public Object intercept(Executor executor, Object[] args) throws Throwable {
		if (ArrayUtils.isEmpty(args)) {
			return executor.execute(args);
		}

		ParameterDescriptor[] parameterDescriptors = executor.getParameterDescriptors();
		for (int i = 0; i < parameterDescriptors.length && i < args.length; i++) {
			Object arg = args[i];
			if (arg != null) {
				continue;
			}

			args[i] = getDefaultParameterValue(executor, parameterDescriptors[i]);
		}

		Object returnValue = executor.execute(args);
		if (returnValue == null) {
			returnValue = getDefaultReturnValue(executor);
			if (returnValue != null) {
				returnValue = conversionService.convert(returnValue, executor.getReturnTypeDescriptor());
			}
		}
		return returnValue;
	}

	protected Object getDefaultParameterValue(Executor executor, ParameterDescriptor parameterDescriptor) {
		return getDefaultValue(executor, parameterDescriptor.getTypeDescriptor());
	}

	protected Object getDefaultReturnValue(Executor executor) {
		return getDefaultValue(executor, executor.getReturnTypeDescriptor());
	}

	protected abstract Object getDefaultValue(Executor executor, TypeDescriptor typeDescriptor);
}
