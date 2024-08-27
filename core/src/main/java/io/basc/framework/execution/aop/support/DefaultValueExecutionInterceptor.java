package io.basc.framework.execution.aop.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConversionServiceAware;
import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Function;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.util.Elements;
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
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		if (args.isEmpty()) {
			return function.execute(args);
		}

		Elements<Object> newArgs = function.getParameterDescriptors().parallel(args).filter((e) -> e.isPresent())
				.map((e) -> {
					if (e.getRightValue() != null) {
						return e.getRightValue();
					} else {
						return getDefaultParameterValue(function, e.getLeftValue());
					}
				});
		Object returnValue = function.execute(newArgs);
		if (returnValue == null) {
			returnValue = getDefaultReturnValue(function);
			if (returnValue != null) {
				returnValue = conversionService.convert(returnValue, function.getReturnTypeDescriptor());
			}
		}
		return returnValue;
	}

	protected Object getDefaultParameterValue(Executor executor, ParameterDescriptor parameterDescriptor) {
		return getDefaultValue(executor, parameterDescriptor.getTypeDescriptor());
	}

	protected Object getDefaultReturnValue(Function function) {
		return getDefaultValue(function, function.getReturnTypeDescriptor());
	}

	protected abstract Object getDefaultValue(Executor executor, TypeDescriptor typeDescriptor);
}
