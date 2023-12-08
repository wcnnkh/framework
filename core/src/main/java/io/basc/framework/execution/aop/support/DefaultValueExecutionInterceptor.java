package io.basc.framework.execution.aop.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.element.Elements;
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
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (args.isEmpty()) {
			return executor.execute(args);
		}

		Elements<Object> newArgs = executor.getParameterDescriptors().parallel(args).filter((e) -> e.isPresent())
				.map((e) -> {
					if (e.getRightValue() != null) {
						return e.getRightValue();
					} else {
						return getDefaultParameterValue(executor, e.getLeftValue());
					}
				});
		Object returnValue = executor.execute(newArgs);
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
