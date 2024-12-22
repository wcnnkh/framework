package io.basc.framework.core.execution.aop.support;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		if (args.length == 0) {
			return function.execute(args);
		}

		Elements<Object> newArgs = function.getParameterDescriptors().parallel(Elements.forArray(args))
				.filter((e) -> e.isPresent()).map((e) -> {
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
